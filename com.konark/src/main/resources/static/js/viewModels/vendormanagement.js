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
	"ojs/ojtable"
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
			self.categoryFilterValue = ko.observable();
			self.categoryOptions = ko.observableArray([]);
			self.vendorItemsLoading = ko.observable(false);
			self.vendorItemsLoaded = ko.observable(false);

			self.filePickerAccept = ['.xls', '.xlsx', '.csv'];

			self.messagesDataprovider = ko.pureComputed(function () {
				return new oj.ArrayDataProvider(self.uploadMessages(), {
					keyAttributes: 'id'
				});
			});

			self.vendorItemsColumns = [{
				headerText: 'Vendor Part #',
				field: 'vendorPartNumber'
			}, {
				headerText: 'Status',
				field: 'status'
			}, {
				headerText: 'Item Name',
				field: 'itemName'
			}, {
				headerText: 'Category',
				field: 'category'
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

			self.filteredVendorItems = ko.pureComputed(function () {
				var selectedCategory = self.categoryFilterValue();
				var items = self.vendorItems();
				if (!selectedCategory || selectedCategory === 'All') {
					return items;
				}
				var selectedLower = String(selectedCategory).toLowerCase();
				return (items || []).filter(function (item) {
					return String(item.category || '').toLowerCase() === selectedLower;
				});
			});

			self.categoryFilterDP = ko.pureComputed(function () {
				return new oj.ArrayDataProvider(self.categoryOptions(), {
					keyAttributes: 'value'
				});
			});

			self.vendorItemsDataProvider = ko.pureComputed(function () {
				return new oj.ArrayDataProvider(self.filteredVendorItems(), {
					keyAttributes: 'id'
				});
			});

			self.applyVendorItems = function (items) {
				var normalizedItems = normalizeVendorItems(items || []);
				self.vendorItems(normalizedItems);
				updateCategoryOptions(normalizedItems);
				self.vendorItemsLoaded(true);
				self.vendorItemsLoading(false);
			};

			self.isVendorSelected = ko.pureComputed(function () {
				return !!self.vendorSelectValue();
			});

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

			self.vendorValueActionHandler = function (event) {
				var vendorNumber = event.detail.value;
				self.vendorSelectValue(vendorNumber);
				self.resetUploadState();
				self.vendorItems([]);
				self.categoryFilterValue(null);
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
					updateCategoryOptions([]);
					self.categoryFilterValue(null);
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
				normalized.category = normalized.category || '';
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

		function updateCategoryOptions(items) {
			var categories = [{
				value: 'All',
				label: 'All'
			}];
			var seen = new Set(['all']);
			(items || []).forEach(function (item) {
				var category = (item.category || '').trim();
				if (category && !seen.has(category.toLowerCase())) {
					seen.add(category.toLowerCase());
					categories.push({
						value: category,
						label: category
					});
				}
			});
			categories.sort(function (a, b) {
				return a.label > b.label ? 1 : (b.label > a.label ? -1 : 0);
			});
			self.categoryOptions(categories);
			if (self.categoryFilterValue() && !seen.has(String(self.categoryFilterValue()).toLowerCase())) {
				self.categoryFilterValue(null);
			}
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

			self.loadVendors();
		}

		return VendorManagementViewModel;
	}
);
