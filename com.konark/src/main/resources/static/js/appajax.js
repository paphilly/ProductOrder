"use strict";
define( [ 'ojs/ojcore', 'knockout', 'jquery',   'ojs/ojknockout' ],

function ( oj, ko, $ ) {

	var	TARGET_BLANK = "_blank";
	var textStatus, errorThrown;
	
	$.ajaxSetup({cache:false});

	//app AJAX Functions
	var app = {
			ajax : function ( type, url, done, data, datatype, contentType, headers, sync ) {
				var async = true;
				if ( typeof(sync) == "boolean" ) {
					async = sync;
				}
				$.ajax( {
					xhrFields : function () {
						withCredentials: ( async ) ? false : true;
					},
					beforeSend : function ( xhr ) {
						xhr.setRequestHeader( 'jSessionIDHeader', sessionStorage.getItem( 'jSessionIDHeader' ) );
					},
					async : async,
					crossDomain : false,
					type : ( type ) ? type : "GET",
					dataType : datatype,
					data : data,
					url : url,
					contentType : contentType,
					headers : headers
				} ).done( function (  data, textStatus, request ){
					   done(data, request); })
					.fail( function ( jqXHR, textStatus ) {
						if ( 200 == jqXHR.status ) {
							 done(jqXHR); // when status is 202 accepted
						}
						else if ( 0 !== jqXHR.status ) {
							EXCEPTION( jqXHR );
							oj.Router.sync().then( function () {
								oj.Router.rootInstance.go( 'exception' );
							} );
						}
					} );
			},
			getJSON: function (url)  {
				 var jsonResponse = undefined;   
				 $.ajaxSetup({
				        async: false
				    });

				    $.getJSON( url, function(response) {
				    	jsonResponse = response;
					});

				    $.ajaxSetup({
				        async: true
				    });
				    return jsonResponse;
			},
		};
		return app;
	} );