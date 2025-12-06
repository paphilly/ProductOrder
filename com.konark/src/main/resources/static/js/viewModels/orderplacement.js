define(['require', 'ojs/ojcore', 'knockout', 'knockout', 'ojs/ojrouter', 'appController', 'appajax', 'ojs/ojrouter', 'ojs/ojknockout',
        'ojs/ojbutton', "ojs/ojarraydataprovider", "ojs/ojselectsingle", "ojs/ojselectcombobox", "ojs/ojinputsearch", 'ojs/ojtable', "ojs/ojprogress-bar",
        "ojs/ojinputnumber", "ojs/ojanimation", "ojs/ojlabelvalue", "ojs/ojlabel", 'ojs/ojmessages', "ojs/ojlistdataproviderview"
    ],
    function(require, oj, ko, knockoutMap) {
        var app = require('appajax');
        var appControls = require('appController');

        function ViewModel() {
            var self = this;

            self.loadingStatus = {
                isBusy: ko.observable(true),
                isLoaded: ko.observable(false)
            };

            self.showMessage = ko.observable(false);
            self.productOrderLoading = ko.observable(false);
            self.disabledClass = ko.observable();
            self.orderTotalValue = ko.observable(0);
            self.vendorSelectValue = ko.observable();
            self.deptSelectValue = ko.observable(['All']);
            self.categoryFilterValue = ko.observable('All');
            self.categoryOptions = ko.observableArray([]);
            self.searchValue = ko.observable();
            self.searchRawValue = ko.observable('searchrawvalue');
            self.searchTerm = ko.observable('searchterm');
            self.inventorySearchValue = ko.observable();
            self.inventorySearchRawValue = ko.observable('searchrawvalue');
            self.productTableColumns = ko.observable();
            self.isProductTableLoad = ko.observable(false);
            self.isProductTableLoadisBusy = ko.observable(false);
            self.vendorItemsDataBackup = ko.observable();
            self.isDeptsLoaded = ko.observable(false);
            self.isDeptsBusy = ko.observable(false);
            self.showAllProductsSwitchValue = ko.observable(true);
            self.storeInventorySwitchValue = ko.observable(true);
            self.messagesDataprovider = ko.observable([]);
            self.showVendorReferenceTable = ko.observable(false);
            self.showVendorReferenceSection = ko.observable(false);
            self.showInventoryTable = ko.observable(false);
            self.showVendorReferenceTableLoading = ko.observable(false);
            self.showInventoryTableLoading = ko.observable(false);
            self.inventoryItemsDataBackup = ko.observable([]);
            self.isUpdateOrder = ko.observable(false);
            self.salesHistoryTable = ko.observable(false);
            self.salesHistoryTableLoading = ko.observable(false);
            self.storeSelectValue = ko.observable();
            self.productTableRefreshTimer = null;

            self.productCategoryFilterDP = ko.pureComputed(function() {
                return new oj.ArrayDataProvider(self.categoryOptions(), {
                    keyAttributes: "value"
                });
            });

            AnimationFunction = oj.AnimationUtils['fadeIn'];




            self.loadStores = function() {
            var params = {};
            params.username = localStorage.getItem('loggedInUser');
                var apiURL = appControls.serviceURL("stores", params);
                app.ajax("GET", apiURL, function(responseModel) {
                    self.storeDP = new oj.ArrayDataProvider(buildStores(responseModel.data.Stores), {
                        keyAttributes: "value",
                    });
                    self.storeSelectValue(responseModel.data.Stores[Object.keys(responseModel.data.Stores)[0]]);
                }, "", "", "application/json");

            };

           // self.loadStores();

                   self.storeDP = new oj.ArrayDataProvider(buildStores(JSON.parse(localStorage.userJson).stores), {
                                   keyAttributes: "value",
                           });
                   self.storeSelectValue(JSON.parse(localStorage.userJson).stores[Object.keys(JSON.parse(localStorage.userJson).stores)[0]]);



            self.loadVendors = function() {
                self.showMessage(false);

                var apiURL = appControls.serviceURL("vendors");
                app.ajax("GET", apiURL, function(responseModel) {
                    self.vendorDP = new oj.ArrayDataProvider(buildVendors(responseModel.data.VendorModel.vendors), {
                        keyAttributes: "value",
                    });
                    self.loadingStatus.isBusy(false);
                    self.loadingStatus.isLoaded(true);

                    AnimationFunction(document.getElementById("vendorselectid"), 'fadeIn');
                }, "", "", "application/json");

            };

            self.loadVendors();



            function buildVendors(input) {
                var dataArray = [];
                for (var i = 0; i < input.length; i++) {
                    dataArray.push({
                        "value": input[i].vendorNumber,
                        "label": input[i].company
                    });
                }

                return dataArray.sort((a, b) => (a.label > b.label) ? 1 : ((b.label > a.label) ? -1 : 0));

            }

            function buildStores(input) {
                var dataArray = [];
                for (const prop in input) {
                    dataArray.push({
                        "label": prop,
                        "value": input[prop]
                    });
                }
                return dataArray.sort((a, b) => (a.label > b.label) ? 1 : ((b.label > a.label) ? -1 : 0));

            }



            self.removeAllValue = function(event, data) {
                if (event.target.id === 'deptid') {
                    if (event.detail.value[0] === 'All') {
                        self.deptSelectValue((self.deptSelectValue().filter(item => item !== 'All')));
                    }
                    if (event.detail.value[event.detail.value.length - 1] === 'All') {
                        self.deptSelectValue(['All']);
                    }
                }
            };




            /*Search code*/
            self.handleSearchValueAction = (event) => {
                self.showMessage(false);
                self.isProductTableLoad(false);
                self.isProductTableLoadisBusy(true);
                var baseData = getFilteredProducts();
                var detail = event.detail;
                if (detail.itemContext === null) {
                    setProductTableData(baseData);

                } else {
                    var filteredData = baseData.filter(item => item.vendorItemName === detail.value);
                    setProductTableData(filteredData);

                }

                self.isProductTableLoad(true);
                self.isProductTableLoadisBusy(false);
            };
            /*self.handleSearchValueAction = function(event) {
  self.showMessage(false);
  self.isProductTableLoad(false);
  self.isProductTableLoadisBusy(true);

  var searchValue = event.detail.value ? event.detail.value.trim() : "";

  if (!searchValue) {
    // No search → reset table to full dataset
    self.productTableDataproviderToSort = new oj.ArrayTableDataSource(
      self.vendorItemsDataBackup(),
      { idAttribute: "vendorItemName" }
    );
  } else {
    // Filter data
    var filtered = self.vendorItemsDataBackup().filter(function(item) {
      return item.vendorItemName.toLowerCase().indexOf(searchValue.toLowerCase()) !== -1;
    });
    self.productTableDataproviderToSort = new oj.ArrayTableDataSource(
      filtered,
      { idAttribute: "vendorItemName" }
    );
  }

  // Force table refresh by rebinding
  self.productTableDataprovider = self.productTableDataproviderToSort;

  self.isProductTableLoad(true);
  self.isProductTableLoadisBusy(false);
};*/


            /*Inventory search code starts here*/
            self.handleInventorySearchValueAction = (event) => {
                self.showInventoryTable(false);
                var detail = event.detail;
                if (detail.itemContext === null) {
                    self.inventoryTableDataProvider = new oj.ArrayDataProvider(self.inventoryItemsDataBackup(), {
                        keyAttributes: "itemNumber"
                    });
                } else {
                    var filteredData = self.inventoryItemsDataBackup().filter(item => item.itemNumber === detail.itemContext.key);
                    self.inventoryTableDataProvider = new oj.ArrayDataProvider(filteredData, {
                        keyAttributes: "itemNumber"
                    });
                }

                self.showInventoryTable(true);
            };


    /*Store Value change handler*/
            self.storeValueActionHandler = (event) => {
                self.showMessage(false);
                self.showVendorReferenceSection(true);
                self.showAllProductsSwitchValue(true);
                self.storeInventorySwitchValue(true);
                self.step(0);
                self.isProductTableLoad(false);
              //  self.isProductTableLoadisBusy(true);
               // self.isReviewOrder(false);
               // self.deptSelectValue(['All']);  
               self.vendorSelectValue(['']);               
                
            };


            /*Vendor Value change handler*/
            self.vendorValueActionHandler = (event) => {
                self.showMessage(false);
                self.showVendorReferenceSection(false);
                self.showAllProductsSwitchValue(true);
                self.storeInventorySwitchValue(true);
                var vendorValue = (event.detail.itemContext.data.value);
                self.loadVendorItemsById(vendorValue);
                self.deptSelectValue(['All']);
            };




            /* Load all product based on given vendor id*/
            self.loadVendorItemsById = function(vendorId) {
                self.step(0);
                self.isProductTableLoad(false);
                self.isProductTableLoadisBusy(true);
                self.isReviewOrder(false);
                var urlParms = {};
                urlParms.vendorNumber = vendorId;
              //  urlParms.storeID = '1001';
               urlParms.storeID = self.storeSelectValue();
                var apiURL = appControls.serviceURL("vendorItems/productorder", urlParms);
                app.ajax("GET", apiURL, function(responseModel) {
                    self.vendorItemsDataBackup(responseModel.data.ProductOrderModel.productItems);
                    updateCategoryOptions(self.vendorItemsDataBackup());
                    self.categoryFilterValue('All');
                    var filteredData = getFilteredProducts();
                    setProductTableData(filteredData);
                    self.productTableColumns(buildProductTableColumns());
                    buildSearchDataProvider(filteredData);
                    self.deptSelectManyDP = new oj.ArrayDataProvider(buildDepts(responseModel.data.ProductOrderModel.productItems), {
                        keyAttributes: "value",
                    });

                    self.productDetailsToPost([]);
                    self.isProductTableLoad(true);
                    self.isProductTableLoadisBusy(false);



                    self.loadingStatus.isBusy(false);
                    self.loadingStatus.isLoaded(true);

                }, "", "", "application/json");

            };


            function buildTable(input) {
                var dataArray = [];
                for (var i = 0; i < input.length; i++) {
                    dataArray.push({
                        "value": input[i].vendorNumber,
                        "label": input[i].company
                    });
                }
                return dataArray;

            }



            function buildProductTableColumns() {
                var dataArray = [];
                var headerStyle = "";
                var style = "";
                dataArray.push({
                        "headerText": 'Product Name',
                        "headerClassName": 'kn-table-header-class',
                        "sortProperty": 'vendorItemName',
                        "field": 'productName',
                        "resizable": "enabled",
                        'headerStyle': "color : black;font-size: 1em;font-weight: 600; width: 8em;",
                    },
                    {
                        "headerText": 'PartNumber',
                        "headerClassName": 'kn-table-header-class',
                        "sortProperty": 'vendorPartNumber',
                        "field": 'vendorPartNumber',
                        "resizable": "enabled",
                        'headerStyle': "color : black;font-size: 1em;font-weight: 600; width: 7em;",
                    },
                    {
                        "headerText": 'ItemNumber',
                        "headerClassName": 'kn-table-header-class',
                        "sortProperty": 'itemNumber',
                        "field": 'itemNumber',
                        "resizable": "enabled",
                        'headerStyle': "color : black;font-size: 1em;font-weight: 600; width: 7em;",
                    },
                    {
                        "headerText": 'Category',
                        "sortProperty": 'category',
                        "headerClassName": 'kn-table-header-class',
                        "field": 'category',
                        "resizable": "enabled",
                        'headerStyle': "color : black;font-size: 1em;font-weight: 600; width: 7em;",
                    }, {
                        "headerText": 'Units',
                        "sortProperty": 'unitMeasure',
                        "headerClassName": 'kn-table-header-class',
                        "field": 'unitMeasure',
                        "resizable": "enabled",
                        'headerStyle': "color : black;font-size: 1em;font-weight: 600;  width: 4em;text-align: center;",
                    }, {
                        "headerText": 'Product Price',
                        "sortProperty": 'costPerItem',
                        "headerClassName": 'kn-table-header-class',
                        "field": 'costPerItem',
                        "resizable": "enabled",
                        'headerStyle': "color : black;font-size: 1em;font-weight: 600;width: 4em;text-align: center;",
                    },

                    {
                        "headerText": 'Order Quantity',
                        "sortProperty": 'quantityOrdered',
                        "headerClassName": 'kn-table-header-class',
                        "field": 'quantityOrdered',
                        "resizable": "enabled",
                        'headerStyle': "color : black;font-size: 1em;font-weight: 600;width: 4em;text-align: center;",
                    }


                );
                if (self.storeInventorySwitchValue()) {
                    dataArray.push({
                        "headerText": 'Current Inventory',
                        "sortProperty": 'quantityInStock',
                        "field": 'quantityInStock',
                        "resizable": "enabled",
                        "headerClassName": 'kn-table-header-class',
                        "style": "color : green",
                        'headerStyle': "color : green;font-size: 1em;font-weight: 600;width: 3em; text-align: center;",
                    });
                }
                return dataArray;
            }


            function setProductTableData(data) {
                self.productTableDataproviderToSort = new oj.ArrayDataProvider(data || []);
                self.productTableDataprovider = new oj.ListDataProviderView(self.productTableDataproviderToSort, {
                    sortCriteria: [{
                        attribute: "vendorItemName",
                        direction: "ascending"
                    }],
                });
            }

            function applyCategoryFilter(data) {
                var category = self.categoryFilterValue();
                if (!category || category === 'All') {
                    return data || [];
                }
                var match = String(category).toLowerCase();
                return (data || []).filter(function(item) {
                    return String(item.category || '').toLowerCase() === match;
                });
            }

            function applyDeptFilter(data) {
                var depts = self.deptSelectValue ? self.deptSelectValue() : [];
                if (!depts || depts.length === 0 || depts[0] === 'All') {
                    return data || [];
                }
                return (data || []).filter(function(item) {
                    return depts.indexOf(item.department) > -1;
                });
            }

            function applyInventoryFilter(data) {
                if (self.showAllProductsSwitchValue()) {
                    return data || [];
                }
                return (data || []).filter(function(item) {
                    return item.inventoryFlag === true;
                });
            }

            function getFilteredProducts() {
                var base = self.vendorItemsDataBackup() || [];
                var afterCategory = applyCategoryFilter(base);
                var afterDept = applyDeptFilter(afterCategory);
                return applyInventoryFilter(afterDept);
            }


            function buildDepts(input) {
                var dataArray = [];
                dataArray.push({
                    "value": 'All',
                    "label": 'All',
                });
                for (var i = 0; i < input.length; i++) {
                    dataArray.push({
                        "value": input[i].department,
                        "label": input[i].department
                    });
                }
                // filter duplicates and sort.
                return dataArray.filter((v, i, a) => a.findIndex(t => (t.label === v.label && t.value === v.value)) === i).sort((a, b) => (a.label > b.label) ? 1 : ((b.label > a.label) ? -1 : 0));

            }

            function updateCategoryOptions(items) {
                var categories = [{
                    value: 'All',
                    label: 'All'
                }];
                var seen = new Set(['all']);
                (items || []).forEach(function(item) {
                    var category = (item.category || '').trim();
                    var lower = category.toLowerCase();
                    if (category && !seen.has(lower)) {
                        seen.add(lower);
                        categories.push({
                            value: category,
                            label: category
                        });
                    }
                });
                categories.sort(function(a, b) {
                    return a.label > b.label ? 1 : (b.label > a.label ? -1 : 0);
                });
                self.categoryOptions(categories);
                if (!seen.has(String(self.categoryFilterValue()).toLowerCase())) {
                    self.categoryFilterValue('All');
                }
            }


            self.step = ko.observable(0);
            self.progressValue = ko.pureComputed(() => {
                return Math.min(self.step(), 100);
            });
            window.setInterval(() => {
                self.step((self.step() + 1) % 100);
            }, 30);



            /*Department multislect change handler*/
            self.deptChangeHandler = function(event, data) {
                self.showMessage(false);
                self.isProductTableLoad(false);
                self.isProductTableLoadisBusy(true);

                if (event.detail.value[0] === 'All') {
                    self.deptSelectValue((self.deptSelectValue().filter(item => item !== 'All')));
                }
                if (event.detail.value[event.detail.value.length - 1] === 'All') {
                    self.deptSelectValue(['All']);
                }


                var deptArray = self.deptSelectValue();



                var filteredData = getFilteredProducts();
                setProductTableData(filteredData);

                // load search suggestions again
                buildSearchDataProvider(filteredData);



                self.isProductTableLoad(true);
                self.isProductTableLoadisBusy(false);

            };

            // buildSearchDataProvider -- input from load products
            function buildSearchDataProvider(input) {
                input = input || [];
                var dataArray = [];
                for (var i = 0; i < input.length; i++) {
                    dataArray.push({
                        value: input[i].vendorNumber + input[i].vendorPartNumber,
                        label: input[i].vendorItemName
                    });
                }
                self.searchSuggestionsDP = new oj.ArrayDataProvider(dataArray);
            }

            // buildProductSearchInventoryDataProvider -- input from load products
            function buildProductSearchInventoryDataProvider(input) {
                var dataArray = [];
                for (var i = 0; i < input.length; i++) {
                    dataArray.push({
                        value: input[i].itemNumber,
                        label: input[i].vendorItemName
                    });
                }
                self.searchInventorySuggestionsDP = new oj.ArrayDataProvider(dataArray, {
                    keyAttributes: "value",
                });
            }


            // showAllProductsSwitchValue change handler
            self.showAllProductsSwitchValue.subscribe(function(newValue) {
                self.isProductTableLoad(false);
                self.isProductTableLoadisBusy(true);

                var filteredData = getFilteredProducts();
                setProductTableData(filteredData);
                buildSearchDataProvider(filteredData);

                self.isProductTableLoad(true);
                self.isProductTableLoadisBusy(false);

            });

            self.categoryFilterValue.subscribe(function() {
                self.isProductTableLoad(false);
                self.isProductTableLoadisBusy(true);
                var filteredData = getFilteredProducts();
                setProductTableData(filteredData);
                buildSearchDataProvider(filteredData);
                self.isProductTableLoad(true);
                self.isProductTableLoadisBusy(false);
            });


            // storeInventorySwitchValue change handler
            self.storeInventorySwitchValue.subscribe(function(newValue) {
                self.isProductTableLoad(false);
                self.isProductTableLoadisBusy(true);
                self.productTableColumns(buildProductTableColumns());

                self.isProductTableLoad(true);
                self.isProductTableLoadisBusy(false);
            });


            self.productDetailsToPost = ko.observableArray([]);


            /*Order qunatity update handler*/
            self.orderQuantityUpdateHandler = (event) => {
                var itemID = event.currentTarget.id;
                var itemQty = event.detail.value;

                var filteredDataWithID = self.vendorItemsDataBackup().filter(item => item.vendorNumber + item.vendorPartNumber === itemID);
                filteredDataWithID[0].quantityOrdered = itemQty;
                if (self.productDetailsToPost().filter(item => item.vendorNumber + item.vendorPartNumber === filteredDataWithID[0].vendorNumber + filteredDataWithID[0].vendorPartNumber).length > 0) {
                    self.productDetailsToPost().splice(self.productDetailsToPost().findIndex(e => e.vendorNumber + e.vendorPartNumber === (filteredDataWithID[0].vendorNumber + filteredDataWithID[0].vendorPartNumber)), 1);
                    self.productDetailsToPost().push(filteredDataWithID[0]);
                } else {
                    self.productDetailsToPost().push(filteredDataWithID[0]);
                }
                // Remove 0 qty items to post.
                self.productDetailsToPost(self.productDetailsToPost().filter(e => e.quantityOrdered !== 0));
                buildOrderTotalValue();
            };

            function buildOrderTotalValue() {
                var sum = 0;
                for (var i = 0; i < self.productDetailsToPost().length; i++) {
                    if (self.productDetailsToPost()[i].quantityOrdered > 0) {
                        sum = sum + (parseFloat(self.productDetailsToPost()[i].quantityOrdered) * parseFloat(self.productDetailsToPost()[i].caseCost));
                    }
                }
                self.orderTotalValue(sum.toFixed(2));
            }




            self.placeOrderAction = (event) => {
                self.showMessage(false);

                if (self.productDetailsToPost().length < 1) {
                    showMessages("error", "Select atleast one product to place order");
                    self.showMessage(true);
                    return;
                }

                var postModel = {};
                self.disabledClass('kn-disabled');
                postModel.productItems = self.productDetailsToPost();
               // postModel.storeID = '1001'; //self.storeSelectValue();
                 postModel.storeID = self.storeSelectValue();
                postModel.vendorNumber = self.vendorSelectValue();
                self.productOrderLoading(true);
                var apiURL = appControls.serviceURL('productorder');
                app.ajax("POST", apiURL, function(responseModel) {
                    self.disabledClass(null);
                    showMessages("confirmation", "Order '" + responseModel.data.productOrderID + "' is placed" );
                    self.productOrderLoading(false);
                    self.showMessage(true);
                    window.setTimeout(function() {
                        self.showMessage(false);
                    }, 5000);
                    if (self.productTableRefreshTimer) {
                        clearTimeout(self.productTableRefreshTimer);
                    }
                     resetProductTableToVendorItems();
                      var productTable = document.getElementById('producttableid');
                        if (productTable && typeof productTable.refresh === 'function') {
                            productTable.refresh();
                        }
                    // Allow the confirmation message to stay visible before refreshing the table.
                    
                }, knockoutMap.toJSON(postModel), 'JSON', 'application/json');
            };




            /*Place order action*/

            self.updateOrderAction = (event) => {
                self.showMessage(false);
                self.deptSelectValue(['All']);
                self.isUpdateOrder(true);
                self.isReviewOrder(false);
                self.isProductTableLoad(false);
                self.isProductTableLoadisBusy(true);
                setProductTableData(getFilteredProducts());

                self.isProductTableLoad(true);
                self.isProductTableLoadisBusy(false);
            }

            /*Review order action*/
            self.isReviewOrder = ko.observable(false);
            self.reviewOrderAction = (event) => {
                self.showMessage(false);
                self.isReviewOrder(true);
                self.isProductTableLoad(false);
                self.isProductTableLoadisBusy(true);
                self.productTableDataproviderToSort = new oj.ArrayDataProvider(self.productDetailsToPost());
                self.productTableDataprovider = new oj.ListDataProviderView(self.productTableDataproviderToSort, {
                    sortCriteria: [{
                        attribute: "vendorItemName",
                        direction: "ascending"
                    }],
                });

                self.isProductTableLoad(true);
                self.isProductTableLoadisBusy(false);
            }

            function resetProductTableToVendorItems() {
                self.isReviewOrder(false);
                self.productDetailsToPost([]);
                self.orderTotalValue(0);
                // Reset all order quantities back to null so the table inputs clear.
                var items = self.vendorItemsDataBackup() || [];
                items.forEach(function(item) {
                    item.quantityOrdered = null;
                });
                self.vendorItemsDataBackup(items);
                self.isProductTableLoad(false);
                self.isProductTableLoadisBusy(true);
                setProductTableData(getFilteredProducts());

                self.isProductTableLoad(true);
                self.isProductTableLoadisBusy(false);
            }



            /*Product table selection listener. used to load vendor reference section */

            self.productTableSelectionListener = function(selectedRowValue) {
                var fullRowData = self.vendorItemsDataBackup().filter(item => item.itemNumber === selectedRowValue);
                var urlParms = {};
                urlParms.itemNumber = fullRowData[0].itemName;
                urlParms.vendorNumber = fullRowData[0].vendorNumber;
                self.showVendorReferenceTable(false);
                self.showInventoryTable(false);
                self.showVendorReferenceTableLoading(true);
                self.showInventoryTableLoading(true);
                self.showVendorReferenceSection(true);



                self.showInventoryTableLoading(true);
                self.showInventoryTable(false);

                self.salesHistoryTableLoading(true);
                self.salesHistoryTable(false);

                var apiURL = appControls.serviceURL("vendorItems/reference", urlParms);
                app.ajax("GET", apiURL, function(responseModel) {
                    self.vendorReferenceTableDataProvider = new oj.ArrayDataProvider(responseModel.data.ProductOrderModel.productItems, {
                        keyAttributes: "itemNumber"
                    });
                    self.showVendorReferenceTableLoading(false);
                    self.showVendorReferenceTable(true);


                }, "", "", "application/json");

                var urlParms1 = {};
              //  urlParms1.storeID = '1001';
                urlParms1.storeID = self.storeSelectValue();
                urlParms1.productName = fullRowData[0].vendorItemName;
                var apiURL1 = appControls.serviceURL("inventory/product", urlParms1);
                app.ajax("GET", apiURL1, function(responseModel) {
                    self.inventoryItemsDataBackup(responseModel.data.InventoryModel.productItems);
                    self.inventoryTableDataProvider = new oj.ArrayDataProvider(responseModel.data.InventoryModel.productItems, {
                        keyAttributes: "itemNumber"
                    });
                    buildProductSearchInventoryDataProvider(responseModel.data.InventoryModel.productItems)
                    self.showInventoryTableLoading(false);
                    self.showInventoryTable(true);
                }, "", "", "application/json");


                var urlParms2 = {};
                urlParms2.itemNumber = fullRowData[0].itemNumber;
                urlParms2.storeID = self.storeSelectValue();
                var apiURL2 = appControls.serviceURL("salesHistory", urlParms2);
                app.ajax("GET", apiURL2, function(responseModel) {
                    self.salesHistoryTableDataProvider = new oj.ArrayDataProvider(responseModel.data.Sales, {
                        keyAttributes: "vendorItemName"
                    });
                    self.salesHistoryTableLoading(false);
                    self.salesHistoryTable(true);
                }, "", "", "application/json");


            };




            self.dialogStartAnimationListener = (event) => {
                let ui = event.detail;
                if (!event.target.classList.contains("oj-dialog")) return;
                if (ui.action === "open") {
                    event.preventDefault();
                    let options = {
                        duration: "700ms"
                    };
                    let AnimationFunction;
                    AnimationFunction = oj.AnimationUtils['zoomIn'];
                    AnimationFunction(ui.element, options).then(ui.endCallback);
                } else if (ui.action === "close") {
                    event.preventDefault();
                    ui.endCallback();
                }
            };

            self.handleOKClose = function(event, data) {
                $('#underDevDialog').ojDialog('close');
            }

            self.messagesDataprovider = ko.observable([]);
            function showMessages(param1, param2, param3) {
                self.messages = [{
                    severity: param1,
                    summary: param2,
                    detail: param3,
                }];
                self.messagesDataprovider = new oj.ArrayDataProvider(self.messages);
            }




        }

        return ViewModel;
    }
);
