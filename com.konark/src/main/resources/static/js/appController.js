define(['knockout', 'ojs/ojmodule-element-utils', 'ojs/ojknockouttemplateutils', 'ojs/ojrouter', 'ojs/ojresponsiveutils', 'ojs/ojresponsiveknockoututils', 'ojs/ojarraydataprovider',
		'ojs/ojoffcanvas', 'ojs/ojmodule-element', 'ojs/ojknockout', 'ojs/ojcolorspectrum', 'ojs/ojcolor', 'ojs/ojdialog', 'appajax', 'ojs/ojavatar', 'ojs/ojconverterutils-i18n',
		'ojs/ojknockouttemplateutils', "ojs/ojdialog", "ojs/ojinputtext", "ojs/ojcheckboxset", "ojs/ojradioset", 'ojs/ojmessages',
	],
	function (ko, moduleUtils, KnockoutTemplateUtils, Router, ResponsiveUtils, ResponsiveKnockoutUtils, ArrayDataProvider, OffcanvasUtils, ConverterUtilsI18n) {
		var app = require('appajax');

		function ControllerViewModel() {
			var self = this;

			// Optional override for the API base URL. Set window.APIBaseUrl to use a custom base; fallback to browser-derived URL otherwise.
			//const APIBaseUrl = "http://localhost:8080/";
			const APIBaseUrl = null;
			var configuredApiBaseUrl = normalizeBaseUrl(APIBaseUrl ?? window.APIBaseUrl);


			self.initials = ko.observable();
			self.appName = ko.observable();
			self.userLogin = ko.observable();
			self.userAuthenticated = ko.observable();
			self.loggedInUser = ko.observable();

			self.loginError = ko.observable(false);
			self.newFirstName = ko.observable();
			self.newLastName = ko.observable();
			self.newUserName = ko.observable();
			self.newEmail = ko.observable();
			self.newPassword = ko.observable();
			self.newRole = ko.observable();
	      	self.createUserTabSelectedItem = ko.observable("Create");

			// Media queries for repsonsive layouts
			var smQuery = ResponsiveUtils.getFrameworkQuery(ResponsiveUtils.FRAMEWORK_QUERY_KEY.SM_ONLY);
			self.smScreen = ResponsiveKnockoutUtils.createMediaQueryObservable(smQuery);
			var mdQuery = ResponsiveUtils.getFrameworkQuery(ResponsiveUtils.FRAMEWORK_QUERY_KEY.MD_UP);
			self.mdScreen = ResponsiveKnockoutUtils.createMediaQueryObservable(mdQuery);

			// Router setup
			self.router = Router.rootInstance;
			self.router.configure({
				'home': {
					label: 'Home'
				},
				'Order Placement': {
					label: 'Order Placement',
					isDefault: true,
				},
				'Accounts Payable': {
					label: 'Accounts Payable'
				},
				'Store Dashboard': {
					label: 'Store Dashboard'
				},

				'Gift Card': {
					label: 'Gift Card'
				},
				'Vendor Management': {
					label: 'Vendor Management'
				}
			});
			Router.defaults['urlAdapter'] = new Router.urlParamAdapter();


			// Home button click handler
			self.homeButtonHandler = function () {
				self.router.go('Order Placement');
			}


			function clearLocalSession() {
				var hours = 1; // Reset when storage is more than 24hours
				var now = new Date().getTime();
				var setupTime = localStorage.getItem('setupTime');
				if (now - setupTime > hours * 60 * 60 * 1000) {
					// if(now-setupTime >  60*1000) { //when 60 seconds
					localStorage.clear();
				}

				self.initials(localStorage.getItem('initials'));
				self.loggedInUser(localStorage.getItem('loggedInUser'));
			}

			clearLocalSession();


			//self.selectedTab = ko.observable( localStorage.authenticated === 'true' ? undefined : 'login');
			self.headerTabSelectedItem = ko.observable(localStorage.getItem('authenticated') === 'true' ? localStorage.getItem('selectedHeaderTabName') : 'login');


			self.loadModule = function () {
				self.moduleConfig = ko.pureComputed(function () {
					var name = self.router.moduleConfig.name();
					self.headerTabSelectedItem(name) // setting the reloaded tab value to be selected
					var viewPath = 'views/' + name.toLowerCase().replace(/\s/g, '') + '.html'; // to lower case and replace space to match file names
					var modelPath = 'viewModels/' + name.toLowerCase().replace(/\s/g, '');
					return moduleUtils.createConfig({
						viewPath: viewPath,
						viewModelPath: modelPath,
						params: {
							parentRouter: self.router
						}
					});
				});
			};
			/**
			 * Function to build baseServiceURL
			 *
			 * @return
			 */
			function baseServiceURI() {
				var protocol = window.location.protocol;
				var hostName = window.location.hostname;
				var port = window.location.port;
				// var port = '8080'
				var pathName = window.location.pathname.substring(0, window.location.pathname.lastIndexOf('/'));
				var servicePath = "";
				var baseURI = protocol.concat("//").concat(hostName).concat(":").concat(port).concat(pathName).concat(servicePath)
					.concat("/");
				return baseURI;
			}

			function normalizeBaseUrl(url) {
				if (!url) {
					return null;
				}
				return url.endsWith("/") ? url : url.concat("/");
			}

			// Build URL
			self.serviceURL = function (serviceName, params) {
				var baseUrl = configuredApiBaseUrl || baseServiceURI();

				if (!params && serviceName) {
					return baseUrl.concat(serviceName);
				} else if (params && serviceName) {
					return baseUrl.concat(serviceName).concat("?").concat($.param(params));
				} else {
					return undefined;
				}
			}

			self.tabsDataProvider = ko.observable([]);
			if (localStorage.getItem('pages')) {
				var storedPages = ensureVendorPage(JSON.parse(localStorage.getItem('pages')));
				localStorage.setItem('pages', JSON.stringify(storedPages));
				self.tabsDataProvider(new oj.ArrayDataProvider(storedPages, {
					keyAttributes: "id"
				}));
			}
			//     self.tabsDataProvider = new oj.ArrayDataProvider(data, {  keyAttributes: "id"  });


			self.configureScreens = function (inputData) {
				self.tabsDataProvider = ko.observableArray([]);
				var data = buildScreens(inputData.pages);
				data = ensureVendorPage(data);
				localStorage.setItem('pages', JSON.stringify(data));
				self.tabsDataProvider(new oj.ArrayDataProvider(data, {
					keyAttributes: "id"
				}));
				self.headerTabSelectedItem(inputData.pages[0]);
				self.router.go(inputData.pages[0]);

			};

			function buildScreens(inputData) {
				var dataArray = [];
				for (var a = 0; a < inputData.length; a++) {
					dataArray.push({
						name: inputData[a],
						id: inputData[a]
					});
				}
				return ensureVendorPage(dataArray);
			}

			function ensureVendorPage(pages) {
				var exists = pages.some(function (item) {
					return item.name === 'Vendor Management' || item.id === 'Vendor Management';
				});
				if (!exists) {
					pages.push({
						name: 'Vendor Management',
						id: 'Vendor Management'
					});
				}
				return pages;

			}


			self.appLogin = function (username, password) {
				var postModel = {};
				postModel.username = username;
				postModel.password = password;

				var apiURL = self.serviceURL('login');
				app.ajax("POST", apiURL, function (responseModel, username) {

					if (responseModel.data.UserModel.userJSON != null) {
						self.initials(JSON.parse(responseModel.data.UserModel.userJSON).firstname.substring(0, 1).toUpperCase() + JSON.parse(responseModel.data.UserModel.userJSON).lastname.substring(0, 1).toUpperCase());
						self.loggedInUser(responseModel.data.UserModel.username);
						localStorage.setItem('userJson', responseModel.data.UserModel.userJSON);
						self.configureScreens(JSON.parse(responseModel.data.UserModel.userJSON));
						createLocalSession();
						self.userAuthenticated(responseModel.data.UserModel != null);
					} else {
						self.loginError(true);
					}

				}, ko.toJSON(postModel), 'JSON', 'application/json');
			};

			self.userExistsAlready = ko.observable(true);
			self.disableNewUserName = ko.observable(false);
			/* validate new user API call*/
			self.validateUserName = function (username) {
			if(username === undefined) {
			return;
			}
				var postModel = null;
				var params = {};
				params.username = self.newUserName();
				var apiURL = self.serviceURL('administration/users/user', params);
				app.ajax("POST", apiURL, function (responseModel) {
					self.userExistsAlready(responseModel.data.UserExists);
					if (!self.userExistsAlready()) {
						self.disableNewUserName(true);
					}
					if (self.userExistsAlready()) {
						showMessages("error", "This Username is already present.");
						self.showMessage(true);
					}

				}, ko.toJSON(postModel), 'JSON', 'application/json');
			};


			/*Create new user API call*/
			self.createUserName = function (username, password) {
				var postModel = {};
				var params = {};

				if (validateCreateNewInputFields()) {
					return;
				}


				params.username = self.newUserName();
				postModel.username = self.newUserName();
				postModel.password = self.newPassword();
				postModel.role = self.newRole();
				postModel.status = 'true';

				var userJsonToPost = {};
				userJsonToPost.firstname = self.newFirstName();
				userJsonToPost.lastname = self.newLastName();
				userJsonToPost.email = self.newEmail();
				userJsonToPost.role = self.newRole();

				var stores = self.newUserStoresList().reduce(function (obj, key) {
					if (JSON.parse(localStorage.userJson).stores.hasOwnProperty(key)) obj[key] = JSON.parse(localStorage.userJson).stores[key];
					return obj;
				}, {});

				var locations = self.newUserLocationsList().reduce(function (obj, key) {
					if (JSON.parse(localStorage.userJson).locations.hasOwnProperty(key)) obj[key] = JSON.parse(localStorage.userJson).locations[key];
					return obj;
				}, {});

				userJsonToPost.stores = stores;
				userJsonToPost.locations = locations;
				userJsonToPost.pages = self.newUserPagesList();
				postModel.userJSON = ko.toJSON(userJsonToPost);


				var apiURL = self.serviceURL('administration/users/user', params);
				app.ajax("POST", apiURL, function (responseModel) {
					if (responseModel.data.UserCreationSuccess) {
						showMessages("confirmation", 'User created successfully → ' + self.newUserName());
						self.showMessage(true);
					}

				}, ko.toJSON(postModel), 'JSON', 'application/json');
			};


			self.headerTabSelectedItem.subscribe(function (newValue) {
				//self.selectedTab(newValue);
				localStorage.setItem('selectedHeaderTabName', newValue);
				self.router.go(newValue);

			});


			function createLocalSession() {
				var now = new Date().getTime();
				var setupTime = localStorage.getItem('setupTime');
				//create a session object and put current datetime and authenticated parameter
				if (setupTime == null) {
					localStorage.setItem('setupTime', now);
					localStorage.setItem('authenticated', true);
					localStorage.setItem('initials', self.initials());
					localStorage.setItem('loggedInUser', self.loggedInUser());
				}
			}




			self.newUserStoresList = ko.observable([]);
			self.newUserLocationsList = ko.observable([]);
			self.newUserPagesList = ko.observable([]);

			function buildCheckboxDP(data) {
				var dataArray = [];
				for (const property in data) {
					dataArray.push({
						"value": property,
						"label": property
					});
				}
				return dataArray;

			}

			function buildPagesCheckboxDP(input) {
				var dataArray = [];
				for (var i = 0; i < input.length; i++) {
					dataArray.push({
						"value": input[i],
						"label": input[i]
					});
				}
				return dataArray;

			}


			/*Vendor Value change handler*/
			self.newUserChangeHandler = (event) => {
				self.showMessage(false);
				var vendorValue = (event.detail);
				self.validateUserName(event.detail.value);
			};

			self.showMessage = ko.observable(false);

			function showMessages(param1, param2, param3) {
				self.messages = [{
					severity: param1,
					summary: param2,
					detail: param3,
				}];
				self.messagesDataprovider = new oj.ArrayDataProvider(self.messages);
			}

			self.invalidNewPass = ko.observable();
			self.invalidNewFN = ko.observable();
			self.invalidNewLN = ko.observable();
			self.invalidNewEmail = ko.observable();
			self.invalidNewRole = ko.observable();
			self.invalidNewStoresList = ko.observable();
			self.invalidNewLocationList = ko.observable();
			self.invalidNewPagesList = ko.observable();

			function validateCreateNewInputFields() {
				var isInvalid = false;
				if (self.newPassword() === undefined) {
					self.invalidNewPass([{
						summary: "Error",
						detail: "Enter a valid Password",
						severity: "error"
					}]);
					isInvalid = true;
				}

				if (self.newFirstName() === undefined) {
					self.invalidNewFN([{
						summary: "Error",
						detail: "Enter a valid Firstname",
						severity: "error"
					}]);
					isInvalid = true
				}

				if (self.newLastName() === undefined) {
					self.invalidNewLN([{
						summary: "Error",
						detail: "Enter a valid LastName",
						severity: "error"
					}]);
					isInvalid = true
				}
				if (self.newEmail() === undefined) {
					self.invalidNewEmail([{
						summary: "Error",
						detail: "Enter a valid Email",
						severity: "error"
					}]);
					isInvalid = true
				}

				if (self.newRole() === undefined) {
					self.invalidNewRole([{
						summary: "Error",
						detail: "Select a valid Role",
						severity: "error"
					}]);
					isInvalid = true
				}

				if (self.newUserStoresList().length === 0) {
					self.invalidNewStoresList([{
						summary: "Error",
						detail: "Select min one store",
						severity: "error"
					}]);
					isInvalid = true
				}

				if (self.newUserLocationsList().length === 0) {
					self.invalidNewLocationList([{
						summary: "Error",
						detail: "Select min one location",
						severity: "error"
					}]);
					isInvalid = true
				}

				if (self.newUserPagesList().length === 0) {
					self.invalidNewPagesList([{
						summary: "Error",
						detail: "Select min one page",
						severity: "error"
					}]);
					isInvalid = true
				}
				return isInvalid;
			}


			const data = [{
					name: "Create User",
					id: "Create"
				},
				{
					name: "Update User",
					id: "Update"
				}
			];

			self.createUserTabDP = new oj.ArrayDataProvider(data, {
				keyAttributes: "id"
			});


			self.createUserTabSelectedItem.subscribe(function (newValue) {

			});







self.createdNewClicked = ko.observable(false);


			self.userMenuAction = function (event) {

            				if (event.target.value === 'out') {
            					localStorage.clear();
            					window.location.href = window.origin;
            				}
            				if (event.target.value === 'help') {
            					$('#helpDialog').ojDialog('open');
            				}

            				if (event.target.value === 'createNewUser') {
            					self.newFirstName(undefined);
            					self.newLastName(undefined);
            					self.newUserName(undefined);
            					self.newEmail(undefined);
            					self.newPassword(undefined);
            					self.newRole(undefined);
            					self.newUserStoresList([]);
            					self.newUserLocationsList([]);
            					self.newUserPagesList([]);
            					self.disableNewUserName(false);
            					self.userExistsAlready(true);

            					self.invalidNewPass(undefined);
            					self.invalidNewFN(undefined);
            					self.invalidNewLN(undefined);
            					self.invalidNewEmail(undefined);
            					self.invalidNewRole(undefined);
            					self.invalidNewStoresList(undefined);
            					self.invalidNewLocationList(undefined);
            					self.invalidNewPagesList(undefined);

            						self.storesCheckBoxDP = ko.observable(new oj.ArrayDataProvider(buildCheckboxDP(JSON.parse(localStorage.userJson).stores), {
                                        keyAttributes: "value"
                                    }));
                                    self.locationsCheckBoxDP = ko.observable(new oj.ArrayDataProvider(buildCheckboxDP(JSON.parse(localStorage.userJson).locations), {
                                        keyAttributes: "value"
                                    }));
                                    self.pagesCheckBoxDP = ko.observable(new oj.ArrayDataProvider(buildPagesCheckboxDP(JSON.parse(localStorage.userJson).pages), {
                                        keyAttributes: "value"
                                    }));
                                    self.createdNewClicked(true);

            					$('#createUserDialog').ojDialog('open');

            				}

            			}

            			self.closeHelp = function (data, event) {
            				$('#helpDialog').ojDialog('close');
            			}
            			self.closeCreateUserDialog = function (data, event) {
            				$('#createUserDialog').ojDialog('close');
            			}


		}


		return new ControllerViewModel();
	}
);
