define(['require', 'ojs/ojcore', 'knockout', 'knockout', 'ojs/ojrouter', 'appController', 'appajax', 'ojs/ojrouter', 'ojs/ojknockout', 'ojs/ojbutton', "ojs/ojarraydataprovider",
        "ojs/ojselectsingle", "ojs/ojchart", "ojs/ojdatetimepicker", "ojs/ojlistdataproviderview", "ojs/ojselectcombobox", 'ojs/ojtable'],
    function(require, oj, ko, knockoutMap) {
        var app = require('appajax');
        var appControls = require('appController');

        function StoreDashboardViewModel() {
            var self = this;

            self.loadingStatus = {
                isBusy: ko.observable(true),
                isLoaded: ko.observable(false)
            };
            self.vendorSelectValue = ko.observable('All');
            self.locationSelectValue = ko.observable(['All']);
            self.statusSelectValue = ko.observable(['--All--']);
            self.accountsPayableTableShow = ko.observable(false);
            self.accountsPayableTableLoading = ko.observable(true);
            self.accountsPayableData = ko.observable();
            self.showBillStatus = ko.observable(false);
            self.accountsPayableTableDataProvider = ko.observableArray([]);
            self.accountsPayableSelectedData = ko.observable();
            self.locationSelected = ko.observable(false);
            var locationsArrayToPost = [];
            var statusArrayToFilter = [];

            self.showBillDetails = ko.observable(false);
            self.approveActionInProgress = ko.observable(false);
            self.disabledClass = ko.observable();
            self.refreshLocationfilter = ko.observable(false);


            self.loadLocations = function(id) {
                    buildLocationsArrayToPost(JSON.parse(localStorage.userJson).locations);
                    self.locationDP = new oj.ArrayDataProvider(buildLocations(JSON.parse(localStorage.userJson).locations), {
                        keyAttributes: "value",
                    });
                    self.loadVendors(self.locationSelectValue().includes('All') ? locationsArrayToPost : self.locationSelectValue());
                    self.accountspayable(self.locationSelectValue().includes('All') ? locationsArrayToPost : self.locationSelectValue());
                    self.refreshLocationfilter(true);
                    self.loadingStatus.isBusy(false);
                    self.loadingStatus.isLoaded(true);

            };


            self.loadVendors = function(id) {
             self.locationSelected(false);
                var params = {};
                params.locations = id;
                var apiURL = appControls.serviceURL("accountspayable/vendors", params);
                app.ajax("GET", apiURL, function(responseModel) {
                    self.vendorDP = new oj.ArrayDataProvider(buildLocations(responseModel.data.Vendors), {
                        keyAttributes: "value",
                    });
                    self.locationSelected(true);

                }, "", "", "application/json");
            };



            function buildStatus(input) {
                statusArrayToFilter = [];
                var dataArray = [];
                var dataArray1 = [];
                dataArray.push('--All--');
                for (var i = 0; i < input.length; i++) {
                    dataArray.push(input[i].status);
                    statusArrayToFilter.push(input[i].status);
                }

                var uniqueDataArray = [...new Set(dataArray)]

                for (var j = 0; j < uniqueDataArray.length; j++) {
                    dataArray1.push({
                        "value": uniqueDataArray[j],
                        "label": uniqueDataArray[j]
                    });
                }
                return dataArray1;
            }

            function buildLocations(input) {
                var dataArray = [];
                dataArray.push({
                    "value": 'All',
                    "label": '--All--'
                });
                for (const property in input) {
                    dataArray.push({
                        "value": input[property],
                        "label": property
                    });
                }
                
                   dataArray.push({
                        "value": 'UNASSIGNED',
                        "label": 'UNASSIGNED'
                    });
                    
                return dataArray.sort((a, b) => (a.label > b.label) ? 1 : ((b.label > a.label) ? -1 : 0));

            }

            function buildLocationsArrayToPost(input) {
                locationsArrayToPost = [];
                for (const property in input) {
                    locationsArrayToPost.push(input[property]);
                }
                locationsArrayToPost.push('All');
            }

            /*Vendor Value change handler*/
            self.vendorValueActionHandler = (event) => {
                var vendorValue = (event.detail.itemContext.data.value);
                self.accountsPayableTableShow(false);
                if (vendorValue === 'All') {
                    self.accountsPayableTableDataProvider(new oj.ArrayDataProvider(self.accountsPayableData(), {
                        keyAttributes: "id"
                    }));

                } else {
                    var filteredData = self.accountsPayableData().filter(item => item.vendorID === vendorValue);
                    self.accountsPayableTableDataProvider(new oj.ArrayDataProvider(filteredData, {
                        keyAttributes: "id"
                    }));
                }
                self.statusSelectValue(['--All--']) // reset bill status filter value
                self.accountsPayableTableShow(true);

            };

            /*location value change*/
            self.locationValueActionHandler = (event) => {
                // reset vendor and bill status filter values
                self.vendorSelectValue('All');
                self.statusSelectValue(['--All--']);

                self.refreshLocationfilter(false);
                if (event.detail.value[event.detail.value.length - 1] === 'All') {
                    self.locationSelectValue(['All']);
                }
                if (event.detail.value[event.detail.value.length - 1] !== 'All') {
                    self.locationSelectValue((self.locationSelectValue().filter(item => item !== 'All')));
                }


                self.loadVendors(self.locationSelectValue().includes('All') ? locationsArrayToPost : self.locationSelectValue());
                self.accountspayable(self.locationSelectValue().includes('All') ? locationsArrayToPost : self.locationSelectValue());
                self.refreshLocationfilter(true);

            };

            self.statusValueActionHandler = (event) => {
            // filter vendor data first before bill status filter
                var vendorFilteredData;
                if(self.vendorSelectValue() !== "All") {
                       vendorFilteredData = self.accountsPayableData().filter(item => item.vendorID === self.vendorSelectValue());
                 }
                 else {
                    vendorFilteredData = self.accountsPayableData();
                 }


               self.showBillStatus(false);
                if (event.detail.value[event.detail.value.length - 1] === '--All--') {
                    self.statusSelectValue(['--All--']);
                }
                if (event.detail.value[event.detail.value.length - 1] !== '--All--') {
                    self.statusSelectValue((self.statusSelectValue().filter(item => item !== '--All--')));
                }



                var statusValue = (event.detail.value);
                if (self.statusSelectValue().includes('--All--')) {
                    statusValue = statusArrayToFilter;
                     self.accountsPayableTableDataProvider(new oj.ArrayDataProvider(vendorFilteredData, { keyAttributes: "id" }));
                }

                else {
                   var filteredData = [];
                        for (var i = 0; i < statusValue.length; i++) {
                            for (var j = 0; j < vendorFilteredData.length; j++) {
                                if (vendorFilteredData[j].status === statusValue[i]) {
                                    filteredData.push(vendorFilteredData[j]);
                                }
                            }
                        }
                        self.accountsPayableTableDataProvider(new oj.ArrayDataProvider(filteredData, {
                            keyAttributes: "id"
                        }));
                }

                self.accountsPayableTableShow(false);
                self.accountsPayableTableShow(true);
                 self.showBillStatus(true);
            };

            /*Account payable API*/


            self.accountspayable = function(id) {
                var params = {};
                params.locations = id;
                self.accountsPayableTableLoading(true);
                self.accountsPayableTableShow(false);
                self.showBillStatus(false);
                self.statusSelectValue(['--All--']);
                var apiURL = appControls.serviceURL("accountspayable/bills", params);
                app.ajax("GET", apiURL, function(responseModel) {
                    self.accountsPayableData(responseModel.data.AccountsPayable);
                    self.showBillStatus(false);
                    self.accountsPayableTableDataProvider(new oj.ArrayDataProvider(responseModel.data.AccountsPayable, {
                        keyAttributes: "id"
                    }));

                    self.statusDP = new oj.ArrayDataProvider(buildStatus(responseModel.data.AccountsPayable), {
                        keyAttributes: "value"
                    });
                    self.showBillStatus(true);
                    self.accountsPayableTableShow(true);
                    self.accountsPayableTableLoading(false);

                }, "", "", "application/json");

            };



            self.accountsPayableTableSelectionListener = (event) => {
                let selectionText = "";
                const row = event.detail.value.row;
                const column = event.detail.value.column;
                if (row.values().size > 0) {
                    row.values().forEach(function(key) {
                        selectionText += selectionText.length === 0 ? key : ", " + key;
                    });
                }
                self.accountsPayableSelectedData(self.accountsPayableData().find(o => o.id === selectionText));
                self.showBillDetails(true);

            };


            self.approveAction = (event) => {
                self.approveActionInProgress(true);
                var postModel = self.accountsPayableSelectedData();
                if (event.target) {
                    self.accountsPayableSelectedData().status = event.target.getAttribute("buttontype");
                }

                var today = new Date(self.accountsPayableSelectedData().overrideDueDate);
                var dateBackup = self.accountsPayableSelectedData().overrideDueDate;
                var dd = String(today.getDate()).padStart(2, '0');
                var mm = String(today.getMonth() + 1).padStart(2, '0'); //January is 0!
                var yyyy = today.getFullYear();

                today = mm + '/' + dd + '/' + yyyy;
                self.accountsPayableSelectedData().overrideDueDate = today;

                self.disabledClass('kn-disabled');
                var apiURL = appControls.serviceURL('accountspayable');
                app.ajax("PUT", apiURL, function(responseModel) {
                    self.approveActionInProgress(false);


                   /* self.accountsPayableTableDataProvider(new oj.ArrayDataProvider(self.accountsPayableData(), {
                        keyAttributes: "id"
                    }));*/

                    self.accountsPayableSelectedData().status = responseModel.data.AccountsPayable.status; // setting status as per API response
                    self.filterDataOnDemand(); // apply all filters on demand after API.
                    self.disabledClass('');
                    self.accountsPayableSelectedData().overrideDueDate = dateBackup;
                    self.showBillDetails(false);
                    self.showBillDetails(true);


                }, knockoutMap.toJSON(postModel), 'JSON', 'application/json');
            }


      self.loadLocations();

/*This function to filter data after approve button actions*/
           self.filterDataOnDemand = function(event, data){
           var vendorFilteredData;
           if(self.vendorSelectValue() !== "All") {
                var filteredData = self.accountsPayableData().filter(item => item.vendorID === self.vendorSelectValue());
                   vendorFilteredData = filteredData;
                   self.accountsPayableTableDataProvider(new oj.ArrayDataProvider(filteredData, {  keyAttributes: "id"   }));
           }

             else {
                 vendorFilteredData = self.accountsPayableData();
              }

            if (self.statusSelectValue().includes('--All--')) {
                        statusValue = statusArrayToFilter;
                        self.accountsPayableTableDataProvider(new oj.ArrayDataProvider(vendorFilteredData, { keyAttributes: "id" }));
             }

            else {
               var filteredData = [];
               self.showBillStatus(false);
               if(!self.statusSelectValue().includes(self.accountsPayableSelectedData().status)) {
                 self.statusSelectValue().push(self.accountsPayableSelectedData().status); // push the status value that is clicked from approved button action.
               }
                self.showBillStatus(true);
                    for (var i = 0; i < self.statusSelectValue().length; i++) {
                        for (var j = 0; j < vendorFilteredData.length; j++) {
                            if (vendorFilteredData[j].status === self.statusSelectValue()[i]) {
                                filteredData.push(vendorFilteredData[j]);
                            }
                        }
                    }
                    self.accountsPayableTableDataProvider(new oj.ArrayDataProvider(filteredData, {
                        keyAttributes: "id"
                    }));
            }
           }

        }



        return StoreDashboardViewModel;
    }
);
