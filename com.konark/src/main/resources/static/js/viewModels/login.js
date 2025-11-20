define(['require', 'ojs/ojcore', 'knockout', 'ojs/ojrouter', 'appController', 'appajax', 'ojs/ojrouter', 'ojs/ojknockout', 'ojs/ojbutton', "ojs/ojarraydataprovider", "ojs/ojselectsingle",
		'ojs/ojnavigationlist', "ojs/ojdialog", "ojs/ojinputtext"
	],
	function (require, oj, ko, knockoutMap) {
		var app = require('appajax');
		var appControls = require('appController');

		function ViewModel() {
			var self = this;

			self.loadingStatus = {
				isBusy: ko.observable(true),
				isLoaded: ko.observable(false)
			};
			self.appControls = appControls;
			self.invalidUserMsg = ko.observable();
			self.invalidPassMsg = ko.observable();
			self.usernameRawValue = ko.observable();
			self.passwordRawValue = ko.observable();

			self.loginAction = (event) => {

				if (event && self.usernameRawValue() === undefined) {
					var umsg = {
						summary: "Error",
						detail: "Enter a valid Username",
						severity: "error"
					};
					self.invalidUserMsg([umsg]);
					return;
				}
				if (event && self.passwordRawValue() === undefined) {
					var pmsg = {
						summary: "Error",
						detail: "Enter a valid Password",
						severity: "error"
					};
					self.invalidPassMsg([pmsg]);
					return;
				}


				appControls.appLogin(self.usernameRawValue(), self.passwordRawValue());

			}

			self.submitInput = function (event, data, bindingContext) {
				appControls.loginError(false);
				if (event.keyCode === 13) {
					self.loginAction();
				}
			}


			self.clearLoginAction = (event) => {
				self.usernameRawValue(undefined);
				self.passwordRawValue(undefined);
				self.invalidUserMsg(undefined);
				self.invalidPassMsg(undefined);
			}


			self.loadingStatus.isBusy(false);
			self.loadingStatus.isLoaded(true);


		}


		return ViewModel;
	}
);