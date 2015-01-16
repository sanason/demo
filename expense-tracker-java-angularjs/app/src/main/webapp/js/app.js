"use strict";

var expenseTrackerApp = angular.module('expenseTrackerApp',
		['ngRoute', 'expenseTrackerControllers']);

expenseTrackerApp.config(['$routeProvider', function($routeProvider) {
	$routeProvider.when('/', {
		templateUrl: 'partials/home.html',
		controller: 'HomeCtrl'
	})
	.when('/Login', {
		templateUrl: 'partials/login.html',
		controller: 'LoginCtrl'
	})
	.when('/SignUp', {
		templateUrl: 'partials/signup.html',
		controller: 'SignUpCtrl'
	})
	.otherwise({
		redirectTo: '/'
	});
}]);

// If client session contains authentication token, add authentication header to all requests.
expenseTrackerApp.factory('authInterceptor', function ($window, $q) {
	return {
		request: function (config) {
			config.headers = config.headers || {};
			if ($window.sessionStorage.getItem('token')) {
				config.headers.Authorization = 'token ' + $window.sessionStorage.getItem('token');
			}
			return config;
		},
		responseError: function (rejection) {
			if (rejection.status === 401) {
				// handle the case where the user is not authenticated
			}
			return $q.reject(rejection);
		}
	};
});

// Configure $http service to deserialize date-like strings to Date objects.
// Date-like strings are those that match ISO 8601.
expenseTrackerApp.config(["$httpProvider", function ($httpProvider) {
    var regexIso8601 = /^(\d{4})-(\d{2})-(\d{2})T(\d{2}):(\d{2}):(\d{2}(?:\.\d*))(?:Z|(\+|-)([\d|:]*))?$/;

    function isDateString(input) {
    	return angular.isString(input) && input.match(regexIso8601);
    }
    
    function convertDateStringsToDates(input) {
    	if (isDateString(input)) {
    		return new Date(input);
    	}
    	
    	if (angular.isArray(input)) {
    		for (var i=0 ; i < input.length ; i++) {
    			input[i] = convertDateStringsToDates(input[i]);
    		}
    		return input;
    	}
    	
        if (angular.isObject(input)) {
        	for (var key in input) {
        		if (!input.hasOwnProperty(key)) continue;
        		input[key] = convertDateStringsToDates(input[key]);
        	}
        	return input;
        }
        
        return input;
    }
   
    $httpProvider.defaults.transformResponse.push(function(responseData) {
        convertDateStringsToDates(responseData);
        return responseData;
    });
    
    $httpProvider.interceptors.push('authInterceptor');
}]);