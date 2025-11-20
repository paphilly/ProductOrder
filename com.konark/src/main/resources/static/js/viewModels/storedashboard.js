define([ 'require', 'ojs/ojcore', 'knockout', 'ojs/ojrouter', 'appController', 'appajax', 'ojs/ojrouter', 'ojs/ojknockout', 'ojs/ojbutton', "ojs/ojarraydataprovider" ,
 "ojs/ojselectsingle","ojs/ojchart"],
	function (require, oj, ko, knockoutMap) {
		var app = require('appajax');
		var appControls = require('appController');

		function StoreDashboardViewModel() {
		  	 var self = this;

			self.loadingStatus = {
				isBusy: ko.observable(true),
				isLoaded: ko.observable(false)
			};




			  self.storeSelectValue = ko.observable();
                        self.loadStores = function() {
                            var apiURL = appControls.serviceURL("stores");
                            app.ajax("GET", apiURL, function(responseModel) {
                                self.storeDP = new oj.ArrayDataProvider(buildStores(responseModel.data.Stores), {
                                    keyAttributes: "value",
                                });
                                self.storeSelectValue(responseModel.data.Stores[Object.keys(responseModel.data.Stores)[0]]);
                               self.loadingStatus.isBusy(false);
                               self.loadingStatus.isLoaded(true);
                            }, "", "", "application/json");

                        };

                       self.loadStores();

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

            self.storeValueActionHandler = (event) => {
                self.showSalesChart(false);
                self.loadTotalSales();
            };
             self.charTypeChangeHandler = (event) => {
                self.showSalesChart(false);
                self.salesChartType(self.chartTypeSelectValue());
                self.showSalesChart(true);

            };

self.salesChartType = ko.observable('bar');
self.legendFontSize = ko.observable('12px');
self.showSalesChart = ko.observable(false);
self.xAxisLabel= ko.observable('Date');
self.chartTypeSelectValue = ko.observable('bar');


self.chartTypes = [
                  { value: "bar", label: "Bar" },   { value: "combo", label: "Combo" },
                  { value: "line", label: "Line" },
                    { value: "lineWithArea", label: "LineWithArea" },
                  { value: "area", label: "Area" },
                  { value: "funnel", label: "Funnel" },
                  { value: "pyramid", label: "Pyramid" },
                  { value: "pie", label: "pie" }
              ];
              self.chartTypeDP = new oj.ArrayDataProvider(self.chartTypes, {
                  keyAttributes: "value",
              });
                        self.loadTotalSales = function() {
                                        var urlParms = {};
                                        urlParms.storeID = self.storeSelectValue() ? self.storeSelectValue() : 5678 ;
                                        var apiURL = appControls.serviceURL("salesHistory/totalSales",urlParms);
                                        app.ajax("GET", apiURL, function(responseModel) {

                                        self.totalSalesCountDataProvider = new oj.ArrayDataProvider(buildSalesChartData(responseModel.data.Sales), {
                                        					keyAttributes: "id",
                                        				});
                                            self.showSalesChart (true);
                                           self.loadingStatus.isBusy(false);
                                           self.loadingStatus.isLoaded(true);
                                        }, "", "", "application/json");

                                    };
                                     self.loadTotalSales();


                                    function buildSalesChartData(input) {
                                    			var dataArray = [];
                                    			for(const prop in input) {
                                    				dataArray.push({
                                    					"id": prop,
                                    					"series": 'Sales',
                                    					"group": prop,
                                    					"value": parseInt(input[prop])
                                    				});

                                    			}
                                    			return dataArray.reverse();
                                    		}


		}



		return StoreDashboardViewModel;
	}
);