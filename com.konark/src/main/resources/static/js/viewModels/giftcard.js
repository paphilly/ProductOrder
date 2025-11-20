define(['require', 'ojs/ojcore', 'knockout', 'ojs/ojrouter', 'appController', 'appajax', 'ojs/ojrouter', 'ojs/ojknockout', 'ojs/ojbutton', "ojs/ojarraydataprovider",
		"ojs/ojinputtext", "ojs/ojinputnumber", 'ojs/ojbutton', 'ojs/ojtable'
	],
	function (require, oj, ko, knockoutMap) {
		var app = require('appajax');
		var appControls = require('appController');

		function GiftCardViewModel() {
			var self = this;

			self.loadingStatus = {
				isBusy: ko.observable(true),
				isLoaded: ko.observable(false)
			};


			self.phoneNumber = ko.observable();
			self.giftCardNumber = ko.observable();
			self.giftCardModel = ko.observable();
			self.giftCardDetailsLoaded = ko.observable(false);
			self.giftCardDetailsLoading = ko.observable(false);
			self.giftCardTransactionsDP = ko.observableArray([]);
        	self.invalidPhoneMsg = ko.observable();
        	self.invalidNumberMsg = ko.observable();

			self.submitGiftCard = (event) => {


           if ( self.phoneNumber() === undefined || self.phoneNumber() === '' || !self.phoneNumber()) {
                        var umsg = {
                            summary: "Error",
                            detail: "Enter a valid phoneNumber",
                            severity: "error"
                        };
                        self.invalidPhoneMsg([umsg]);
                        return;
           }

           if (self.giftCardNumber() === undefined || self.giftCardNumber() === '') {
                       var umsg = {
                           summary: "Error",
                           detail: "Enter a valid giftCardNumber",
                           severity: "error"
                       };
                       self.invalidNumberMsg([umsg]);
                       return;
          }

				var params = {};
				params.giftCardNumber = self.giftCardNumber();
				params.mobileNumber = self.phoneNumber();
				self.giftCardDetailsLoading(true);



				var apiURL = appControls.serviceURL("giftCard/transactions", params);
				app.ajax("GET", apiURL, function (responseModel) {

					self.giftCardModel(responseModel.data.GiftCardModel);
					self.giftCardTransactionsDP(new oj.ArrayDataProvider(responseModel.data.GiftCardModel.transactions ? responseModel.data.GiftCardModel.transactions : [], {
						keyAttributes: "InvoiceNumber"
					}));

					self.giftCardDetailsLoading(false);
					self.giftCardDetailsLoaded(true);
				}, "", "", "application/json");
			};


			self.loadingStatus.isBusy(false);
			self.loadingStatus.isLoaded(true);

		}


		return GiftCardViewModel;
	}
);