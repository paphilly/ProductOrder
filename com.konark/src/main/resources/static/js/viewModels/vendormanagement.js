define([
	'require',
	'ojs/ojcore',
	'knockout',
	'ojs/ojknockout',
	'ojs/ojbutton',
	"ojs/ojmessages",
	"ojs/ojfilepicker",
	"ojs/ojarraydataprovider"
],
	function (require, oj, ko) {
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

			self.filePickerAccept = ['.xls', '.xlsx', '.csv'];

			self.messagesDataprovider = ko.pureComputed(function () {
				return new oj.ArrayDataProvider(self.uploadMessages(), {
					keyAttributes: 'id'
				});
			});

			self.handleFileSelect = function (event) {
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
				self.uploadStatus('Reading file...');

				var reader = new FileReader();
				reader.onload = function () {
					self.uploadInProgress(false);
					self.uploadStatus('Upload completed for ' + self.selectedFileName());
					self.uploadMessages([{
						id: 'upload-success',
						severity: 'confirmation',
						summary: 'File uploaded',
						detail: 'Successfully processed ' + self.selectedFileName()
					}]);
				};
				reader.onerror = function () {
					self.uploadInProgress(false);
					self.uploadStatus('Upload failed');
					self.uploadMessages([{
						id: 'upload-failed',
						severity: 'error',
						summary: 'Upload failed',
						detail: 'Could not read the file. Try again.'
					}]);
				};

				reader.readAsArrayBuffer(self.selectedFile());
			};

			self.canUpload = ko.pureComputed(function () {
				return !!self.selectedFile() && !self.uploadInProgress();
			});

			self.loadingStatus.isBusy(false);
			self.loadingStatus.isLoaded(true);
		}

		return VendorManagementViewModel;
	}
);
