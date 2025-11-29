define([
	'require',
	'ojs/ojcore',
	'knockout',
	'jquery',
	'appajax',
	'appController',
	'ojs/ojknockout',
	'ojs/ojbutton',
	"ojs/ojmessages",
	"ojs/ojfilepicker",
	"ojs/ojarraydataprovider",
	"ojs/ojselectsingle",
	"ojs/ojtable",
	"ojs/ojinputtext",
	"ojs/ojinputnumber"
],
	function (require, oj, ko, $, app, appController) {
		function VendorManagementViewModel() {
			var self = this;

			self.loadingStatus = {
				isBusy: ko.observable(true),
				isLoaded: ko.observable(false)
			};

			self.selectedFile = ko.observable();
			self.selectedFileName = ko.observable('');
			self.selectedFileSize = ko.observable('');
			self.uploadStatus = ko.observable('');
			self.uploadInProgress = ko.observable(false);
			self.uploadMessages = ko.observableArray([]);

			self.vendorDP = ko.observable();
			self.vendorSelectValue = ko.observable();
			self.vendorItems = ko.observableArray([]);
			self.vendorItemsOriginal = ko.observableArray([]);
			self.vendorItemsLoading = ko.observable(false);
			self.vendorItemsLoaded = ko.observable(false);
			self.tableMessages = ko.observableArray([]);
			self.isSavingVendorItems = ko.observable(false);

			self.filePickerAccept = ['.xls', '.xlsx', '.csv'];

			self.messagesDataprovider = ko.pureComputed(function () {
				return new oj.ArrayDataProvider(self.uploadMessages(), {
					keyAttributes: 'id'
				});
			});

			self.tableMessagesDataprovider = ko.pureComputed(function () {
				return new oj.ArrayDataProvider(self.tableMessages(), {
					keyAttributes: 'id'
				});
			});

			self.vendorItemsColumns = [{
				headerText: 'Vendor Part #',
				field: 'vendorPartNumber'
			}, {
				headerText: 'Item #',
				field: 'itemNumber'
			},  {
				headerText: 'Item Name',
				field: 'itemName'
			}, {
				headerText: 'Cost / Item',
				field: 'costPerItem'
			}, {
				headerText: 'Weight Cost',
				field: 'weightCost'
			}, {
				headerText: 'Case Cost',
				field: 'caseCost'
			}, {
				headerText: '# Items / Case',
				field: 'numberOfItemsPerVendorCase'
			}];

			self.vendorItemsDataProvider = ko.pureComputed(function () {
				return new oj.ArrayDataProvider(self.vendorItems(), {
					keyAttributes: 'id'
				});
			});

			self.applyVendorItems = function (items) {
				var normalizedItems = normalizeVendorItems(items || []);
				self.vendorItems(normalizedItems);
				self.vendorItemsOriginal(cloneItems(normalizedItems));
				self.vendorItemsLoaded(true);
				self.vendorItemsLoading(false);
			};

			self.canResetVendorItems = ko.pureComputed(function () {
				return self.vendorItemsLoaded() && self.vendorItemsOriginal().length > 0 && !self.isSavingVendorItems();
			});

			self.canSaveVendorItems = ko.pureComputed(function () {
				return self.vendorItemsLoaded() && self.vendorItems().length > 0 && !self.isSavingVendorItems();
			});

			self.isVendorSelected = ko.pureComputed(function () {
				return !!self.vendorSelectValue();
			});

			self.resetVendorItems = function () {
				self.tableMessages([]);
				self.vendorItems(cloneItems(self.vendorItemsOriginal()));
			};

			self.resetUploadState = function () {
				self.uploadMessages([]);
				self.selectedFile(undefined);
				self.selectedFileName('');
				self.selectedFileSize('');
				self.uploadStatus('');
				self.uploadInProgress(false);
			};

			self.handleFileSelect = function (event) {
				self.resetUploadState();
				if (!self.isVendorSelected()) {
					self.uploadMessages([{
						id: 'upload-no-vendor',
						severity: 'error',
						summary: 'Select a vendor first',
						detail: 'Choose a vendor to enable uploads.'
					}]);
					return;
				}

				self.uploadMessages([]);
				var files = event.detail.files ? event.detail.files : (event.target.files || []);
				if (!files || files.length === 0) {
					self.selectedFile(undefined);
					self.selectedFileName('');
					self.selectedFileSize('');
					self.uploadStatus('');
					return;
				}

				var file = files[0];
				var fileName = file.name || '';
				var lowerName = fileName.toLowerCase();
				var isValid = self.filePickerAccept.some(function (ext) {
					return lowerName.endsWith(ext);
				});

				if (!isValid) {
					self.selectedFile(undefined);
					self.selectedFileName('');
					self.selectedFileSize('');
					self.uploadStatus('');
					self.uploadMessages([{
						id: 'upload-invalid',
						severity: 'error',
						summary: 'Invalid file type',
						detail: 'Please upload an Excel or CSV file (.xls, .xlsx, .csv).'
					}]);
					return;
				}

				self.selectedFile(file);
				self.selectedFileName(file.name);
				self.selectedFileSize((file.size / 1024).toFixed(1) + ' KB');
				self.uploadStatus('Ready to upload');
			};

			self.uploadFile = function () {
				self.uploadMessages([]);
				if (!self.isVendorSelected()) {
					self.uploadMessages([{
						id: 'upload-missing-vendor',
						severity: 'error',
						summary: 'No vendor selected',
						detail: 'Select a vendor before uploading a file.'
					}]);
					return;
				}

				if (!self.selectedFile()) {
					self.uploadMessages([{
						id: 'upload-missing',
						severity: 'error',
						summary: 'No file selected',
						detail: 'Select an Excel file before uploading.'
					}]);
					return;
				}

				self.uploadInProgress(true);
				self.uploadStatus('Uploading...');

				var formData = new FormData();
				formData.append("file", self.selectedFile());
				formData.append("vendorNumber", String(self.vendorSelectValue()));

				var apiURL = appController.serviceURL("vendorItems/upload");

				$.ajax({
					url: apiURL,
					type: "POST",
					data: formData,
					contentType: false,
					processData: false,
					beforeSend: function (xhr) {
						xhr.setRequestHeader('jSessionIDHeader', sessionStorage.getItem('jSessionIDHeader'));
					},
					success: function (response) {
						var uploadedItems = [];
						var uploadedFileName = self.selectedFileName();
						if (response && response.data && response.data.VendorItemModel && response.data.VendorItemModel.vendorItems) {
							uploadedItems = normalizeVendorItems(response.data.VendorItemModel.vendorItems);
						}

						var merged = mergeUniqueVendorPartNumbers(self.vendorItems(), uploadedItems, self.vendorSelectValue());
						self.applyVendorItems(merged);

						self.uploadInProgress(false);
						self.uploadStatus('Upload completed');
						self.selectedFile(undefined);
						self.selectedFileName('');
						self.selectedFileSize('');
						self.uploadMessages([{
							id: 'upload-success',
							severity: 'confirmation',
							summary: 'File uploaded',
							detail: uploadedItems.length > 0 ? ('Added ' + uploadedItems.length + ' new items for vendor ' + self.vendorSelectValue()) : ('Successfully processed ' + uploadedFileName)
						}]);
					},
					error: function (jqXHR) {
						self.uploadInProgress(false);
						self.uploadStatus('Upload failed');
						self.uploadMessages([{
							id: 'upload-failed',
							severity: 'error',
							summary: 'Upload failed',
							detail: (jqXHR && jqXHR.responseText) ? jqXHR.responseText : 'Could not upload the file. Try again.'
						}]);
					}
				});
			};

			self.canUpload = ko.pureComputed(function () {
				return !!self.selectedFile() && !self.uploadInProgress() && self.isVendorSelected();
			});

			self.validateVendorItems = function () {
				self.tableMessages([]);

				var inputs = document.querySelectorAll('#vendor-items-table oj-input-text, #vendor-items-table oj-input-number');
				var componentsValid = true;
				Array.prototype.forEach.call(inputs, function (element) {
					if (typeof element.validate === 'function') {
						var result = element.validate();
						if (result === 'invalid') {
							componentsValid = false;
						}
					}
				});

				var dataValid = true;
				self.vendorItems().forEach(function (item) {
					var nameMissing = !item.itemName || !String(item.itemName).trim();
					var costInvalid = !isNonNegativeNumber(item.costPerItem);
					var weightInvalid = !isNonNegativeNumber(item.weightCost);
					var caseInvalid = !isNonNegativeNumber(item.caseCost);
					var countInvalid = !isPositiveInteger(item.numberOfItemsPerVendorCase);

					if (nameMissing || costInvalid || weightInvalid || caseInvalid || countInvalid) {
						dataValid = false;
					}
				});

				if (!componentsValid || !dataValid) {
					self.tableMessages([{
						id: 'vendor-items-validation',
						severity: 'error',
						summary: 'Fix validation errors',
						detail: 'Enter a name and valid numeric values (costs >= 0, items per case >= 1) for all rows.'
					}]);
				}

				return componentsValid && dataValid;
			};

			self.saveVendorItems = function () {
				self.tableMessages([]);

				if (!self.isVendorSelected()) {
					self.tableMessages([{
						id: 'vendor-items-no-vendor',
						severity: 'error',
						summary: 'Select a vendor',
						detail: 'Choose a vendor before saving vendor items.'
					}]);
					return;
				}

				if (!self.vendorItemsLoaded() || self.vendorItems().length === 0) {
					self.tableMessages([{
						id: 'vendor-items-empty',
						severity: 'warning',
						summary: 'Nothing to save',
						detail: 'Load vendor items before attempting to save.'
					}]);
					return;
				}

				if (!self.validateVendorItems()) {
					return;
				}

				var payload = {
					vendorItems: self.vendorItems().map(buildVendorItemPayload)
				};

				var apiURL = appController.serviceURL("vendorItems/updateVendorItems");
				self.isSavingVendorItems(true);

				$.ajax({
					url: apiURL,
					type: "PUT",
					data: JSON.stringify(payload),
					contentType: "application/json",
					beforeSend: function (xhr) {
						xhr.setRequestHeader('jSessionIDHeader', sessionStorage.getItem('jSessionIDHeader'));
					},
					success: function () {
						self.tableMessages([{
							id: 'vendor-items-save-success',
							severity: 'confirmation',
							summary: 'Saved',
							detail: 'Vendor items updated successfully.'
						}]);
						self.vendorItemsOriginal(cloneItems(self.vendorItems()));
					},
					error: function (jqXHR) {
						self.tableMessages([{
							id: 'vendor-items-save-failed',
							severity: 'error',
							summary: 'Save failed',
							detail: (jqXHR && jqXHR.responseText) ? jqXHR.responseText : 'Unable to save vendor items. Try again.'
						}]);
					},
					complete: function () {
						self.isSavingVendorItems(false);
					}
				});
			};

			self.vendorValueActionHandler = function (event) {
				var vendorNumber = event.detail.value;
				self.vendorSelectValue(vendorNumber);
				self.resetUploadState();
				self.tableMessages([]);
				self.vendorItems([]);
				self.vendorItemsOriginal([]);
				self.vendorItemsLoaded(false);
				self.loadVendorItems(vendorNumber);
			};

			self.loadVendors = function () {
				var apiURL = appController.serviceURL("vendors");
				app.ajax("GET", apiURL, function (responseModel) {
					self.vendorDP(new oj.ArrayDataProvider(buildVendors(responseModel.data.VendorModel.vendors), {
						keyAttributes: "value"
					}));
					self.loadingStatus.isBusy(false);
					self.loadingStatus.isLoaded(true);
				}, "", "", "application/json");
			};

			self.loadVendorItems = function (vendorNumber) {
				if (!vendorNumber) {
					self.vendorItems([]);
					self.vendorItemsOriginal([]);
					self.vendorItemsLoaded(false);
					self.vendorItemsLoading(false);
					return;
				}
				self.vendorItemsLoading(true);
				self.vendorItemsLoaded(false);

				var apiURL = appController.serviceURL("vendorItems/" + vendorNumber);
				app.ajax("GET", apiURL, function (responseModel) {
					var items = [];
					if (responseModel && responseModel.data && responseModel.data.VendorItemModel && responseModel.data.VendorItemModel.vendorItems) {
						items = responseModel.data.VendorItemModel.vendorItems.map(function (item) {
							return Object.assign({
								id: item.vendorNumber + "-" + item.vendorPartNumber + "-" + item.itemNumber
							}, item);
						});
					}
					self.applyVendorItems(items);
				}, "", "", "application/json");
			};

			function buildVendors(input) {
				var dataArray = [];
				for (var i = 0; i < input.length; i++) {
					dataArray.push({
						"value": input[i].vendorNumber,
						"label": input[i].company
					});
				}
				return dataArray.sort(function (a, b) {
					return a.label > b.label ? 1 : (b.label > a.label ? -1 : 0);
				});
			}

			function normalizeVendorItems(items) {
				return (items || []).map(function (item) {
					var normalized = Object.assign({
						id: item.vendorNumber + "-" + item.vendorPartNumber + "-" + item.itemNumber
					}, item);
					normalized.itemName = normalized.itemName || '';
					normalized.costPerItem = toOptionalNumber(normalized.costPerItem);
					normalized.weightCost = toOptionalNumber(normalized.weightCost);
					normalized.caseCost = toOptionalNumber(normalized.caseCost);
					normalized.numberOfItemsPerVendorCase = toOptionalInteger(normalized.numberOfItemsPerVendorCase);
					return normalized;
				});
			}

			function mergeUniqueVendorPartNumbers(existingItems, newItems, vendorNumber) {
				var merged = existingItems ? existingItems.slice() : [];
				var existingPartNumbers = new Set(merged.filter(function (it) {
					return it.vendorNumber === vendorNumber;
				}).map(function (it) {
					return it.vendorPartNumber;
				}));

				for (var i = 0; i < newItems.length; i++) {
					var item = newItems[i];
					if (item.vendorNumber === vendorNumber && !existingPartNumbers.has(item.vendorPartNumber)) {
						merged.push(item);
						existingPartNumbers.add(item.vendorPartNumber);
					}
				}

				return merged;
			}

			function cloneItems(items) {
				return (items || []).map(function (item) {
					return Object.assign({}, item);
				});
			}

			function toOptionalNumber(value) {
				if (typeof value === 'string') {
					value = value.trim();
				}
				if (value === null || value === undefined || value === '') {
					return null;
				}
				var numberValue = Number(value);
				return Number.isFinite(numberValue) ? numberValue : null;
			}

			function toOptionalInteger(value) {
				if (typeof value === 'string') {
					value = value.trim();
				}
				if (value === null || value === undefined || value === '') {
					return null;
				}
				var intValue = Number(value);
				return Number.isInteger(intValue) ? intValue : null;
			}

			function toNumberOrZero(value) {
				if (typeof value === 'string') {
					value = value.trim();
				}
				var numberValue = Number(value);
				return Number.isFinite(numberValue) ? numberValue : 0;
			}

			function toIntegerOrZero(value) {
				if (typeof value === 'string') {
					value = value.trim();
				}
				var intValue = Number(value);
				return Number.isInteger(intValue) ? intValue : 0;
			}

			function isNonNegativeNumber(value) {
				if (typeof value === 'string') {
					value = value.trim();
				}
				if (value === null || value === undefined || value === '') {
					return false;
				}
				var numberValue = Number(value);
				return Number.isFinite(numberValue) && numberValue >= 0;
			}

			function isPositiveInteger(value) {
				if (typeof value === 'string') {
					value = value.trim();
				}
				if (value === null || value === undefined || value === '') {
					return false;
				}
				var intValue = Number(value);
				return Number.isInteger(intValue) && intValue >= 1;
			}

			function buildVendorItemPayload(item) {
				return {
					vendorNumber: item.vendorNumber,
					vendorPartNumber: item.vendorPartNumber,
					itemNumber: item.itemNumber,
					itemName: (item.itemName || '').trim(),
					costPerItem: toNumberOrZero(item.costPerItem),
					weightCost: toNumberOrZero(item.weightCost),
					caseCost: toNumberOrZero(item.caseCost),
					numberOfItemsPerVendorCase: toIntegerOrZero(item.numberOfItemsPerVendorCase)
				};
			}

			self.loadVendors();
		}

		return VendorManagementViewModel;
	}
);
