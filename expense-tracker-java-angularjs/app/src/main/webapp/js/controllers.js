"use strict";

// TODO Think about login logic when I am more awake

var expenseTrackerControllers = angular.module('expenseTrackerControllers', ['ngGrid', 'config', 'ui.bootstrap']);

expenseTrackerControllers.controller('HomeCtrl', function($window, $rootScope) {
	var authUser = $window.sessionStorage.getItem('authenticatedUser');
	var token = $window.sessionStorage.getItem('token');
	if (authUser && token) {
		$rootScope.loggedIn = true;
	}
});

expenseTrackerControllers.controller('LoginCtrl', function($scope, $rootScope, ENV, $http, $location, $window) {	

	function loginUrl() {
		return ENV.apiEndpoint + "/authenticate";
	}
	
	$scope.hasError = false;

	$scope.login = function() {
		$scope.hasError = false;
		
		$http.post(loginUrl(), {
			username : $scope.username,
			password : $scope.password
		})
		.success(function(data) {
			$window.sessionStorage.setItem('authenticatedUser', angular.toJson(data.user));
			$window.sessionStorage.setItem('token', data.token);
			$rootScope.loggedIn = true;
			$location.path('/');
		})
		.error(function(data, status) {
			$scope.hasError = true;
			$scope.errorMsg = data.message ? data.message : 'Invalid credentials';
		});
	}
});

expenseTrackerControllers.controller('SignUpCtrl', function($scope, $http, $location, ENV, $modal) {

	function usersUrl(url) {
		return ENV.apiEndpoint + "/users" + url;
	}

	$scope.hasError = false;

	$scope.signup = function() {
		$scope.hasError = false;
		var user = {
				username: $scope.username,
				password: $scope.password
		}
		$http.post(usersUrl('/'), user)
		.success(function(data) {
			var modalInstance = $modal.open({
				template: '<div class="modal-body"> \
					         <h4>Account created</h4> \
					       </div> \
					       <div class="modal-footer"> \
					         <button type="button" class="btn btn-default" ng-click="$close()">OK</button> \
					       </div>',
			    windowClass: 'alertModal'
			});
			
			modalInstance.result.then(function(result) {
				$location.path('/');
			});
		})
		.error(function(data, status) {
			$scope.hasError = true;
			$scope.errorMsg = data.message ? data.message : 'Invalid user';
		});
	}

});

expenseTrackerControllers.controller('ExpenseListCtrl',
		function ($scope, $rootScope, $http, $location, ENV, $window, $modal, $filter) {

	var authUserJson = $window.sessionStorage.getItem('authenticatedUser');
	if (authUserJson) {
		var authUser = angular.fromJson(authUserJson);
		$scope.user = {
		   id : authUser.id,
		   username : authUser.username
		}
	}
	
	$scope.logout = function() {
		$window.sessionStorage.removeItem('authenticatedUser');
		$window.sessionStorage.removeItem('token');
		$rootScope.loggedIn = false;
		$location.path('/');
	}

	function expensesUrl(url) {
		return ENV.apiEndpoint + "/users/" + $scope.user.id + "/expenses" + url;
	}

	$http.get(expensesUrl('/')).success(function(data) {
		$scope.expenses = data;
	});
	
	var columnDefs = [
	     {field: 'date', displayName: 'Date', cellFilter: 'date:\'MM/dd/yyyy\'', width: 130, placeholder: 'MM/dd/yyyy',
			  editableCellTemplate: '<input type="date" ng-class="\'colt\' + col.index" ng-input="COL_FIELD" ng-model="COL_FIELD" />'},
	     {field: 'time', displayName: 'Time', cellFilter: 'date:\'h:mm a\'', width: 130, placeholder: 'h:m a',
			  editableCellTemplate: '<input type="time" ng-class="\'colt\' + col.index" ng-input="COL_FIELD" ng-model="COL_FIELD" />'},
	     {field: 'description', displayName: 'Description', width: 250},
		 {field: 'amount', displayName: 'Amount', cellFilter: 'currency', width: 100,
	    	 editableCellTemplate: '<input type="number" ng-class="\'colt\' + col.index" ng-input="COL_FIELD" ng-model="COL_FIELD" />'},
	     {field: 'comment', displayName: 'Comment'},
	     {field: 'id', displayName: '', enableCellEdit: false, width: 45, cellTemplate: 'partials/grid/last-column.html'}
	];

	$scope.gridOptions = {
			data: 'expenses',
			enableCellSelection: true,
			enableCellEditOnFocus: true,
			enableRowSelection: false,
			enableCellEdit: true,
			rowHeight: 40,
			columnDefs: columnDefs
	};
	
	/****** Grid editing ********/
	
	$scope.addRow = function() {
		$scope.expenses.unshift({"date": "", "time": "", "description": "", "amount":"", "comment": ""});
	};

	// When the user finishes editing a cell, save the row
	// TODO Check that oldValue != newValue
	$scope.$on('ngGridEventEndCellEdit', function(event) {
		var row = event.targetScope.row;
		var expense = row.entity;

		if (expense.id) {
			$http.put(expensesUrl('/' + expense.id), expense);
		}
	});
	
	$scope.saveNewRow = function() {
		var expense = this.row.entity;
		
		$http.post(expensesUrl('/'), expense)
		.success(function(data) {
			expense.id = data.id;
		});
	};

	$scope.removeRow = function() {
		var toDeleteId = this.row.entity.id;
		var index = this.row.rowIndex;

		// Check that this isn't a newly added row
		if (toDeleteId) {
			$http.delete(expensesUrl('/' + toDeleteId))
			.success(function() {
				$scope.expenses.splice(index, 1);	
			});
		}
	}
	
	/***** Filtering *********/
	
	$scope.filterOptions = {
			dateFrom: "",
			dateTo: "",
			timeFrom: "",
			timeTo: "",
			description: "",
			amountLessThan: "",
			amountMoreThan: "",
			comment: ""		
	};
	
	$scope.filter = function() {
		var modalInstance = $modal.open({
			templateUrl: "partials/filter-options.html",
			scope: $scope,
			controller: 'FilterOptionsCtrl'
		});
		
		modalInstance.result.then(function(result) {
			$scope.filterOptions = result;
			updateFilter();
		});
	};
	
	var updateFilter = function() {
		var params = {};

		if ($scope.filterOptions.dateFrom || $scope.filterOptions.dateTo) {
			params.date = buildDateParam($scope.filterOptions.dateFrom, $scope.filterOptions.dateTo);
		}

		if ($scope.filterOptions.timeFrom || $scope.filterOptions.timeTo) {
			params.time = buildTimeParam($scope.filterOptions.timeFrom, $scope.filterOptions.timeTo);
		}

		if ($scope.filterOptions.description) params.description = $scope.filterOptions.description;

		if ($scope.filterOptions.amountLessThan || $scope.filterOptions.amountMoreThan) {
			params.amount = buildAmountParam($scope.filterOptions.amountLessThan, $scope.filterOptions.amountMoreThan);
		}

		if ($scope.filterOptions.comment) params.comment = $scope.filterOptions.comment;

		$http.get(expensesUrl('/'), { 'params' : params })
		.success(function(data) {
			$scope.expenses = data;
		});	
	};
	
	var buildDateParam = function(dateFrom, dateTo) {
		var dateParam = '';
		if (dateFrom) {
			dateParam += 'after:';
			dateParam += $filter('date')($scope.filterOptions.dateFrom, 'yyyy-MM-dd');
			dateParam += ' ';
		} if (dateTo) {
			dateParam += 'before:';
			dateParam += $filter('date')($scope.filterOptions.dateTo, 'yyyy-MM-dd');
		}
		return dateParam;
	}
	
	var buildTimeParam = function(timeFrom, timeTo) {
		var timeParam = '';
		if (timeFrom) {
			timeParam += 'after:';
			timeParam += $filter('date')($scope.filterOptions.timeFrom, 'HH:mm');
			timeParam += ' ';
		} if (timeTo) {
			timeParam += 'before:';
			timeParam += $filter('date')($scope.filterOptions.timeTo, 'HH:mm');
		}
		return timeParam;
	}
	
	var buildAmountParam = function(amountLessThan, amountMoreThan) {
		var amountParam = '';
		if (amountLessThan) {
			amountParam += 'lessThan:';
			amountParam += amountLessThan;
			amountParam += ' ';
		} if (amountMoreThan) {
			amountParam += 'moreThan:';
			amountParam += amountMoreThan;
		}
		return amountParam;
	}
	
	/****** Weekly report *********/
	
	$scope.generateReport = function() {
		$modal.open({
			templateUrl: 'partials/weekly-report.html',
			controller: 'WeeklyReportCtrl',
			scope: $scope
		});
	}
});

expenseTrackerControllers.controller('FilterOptionsCtrl', function ($scope) {

	$scope.applyFilter = function() {
		$scope.$close($scope.filterOptions);	
	}
	
	$scope.clearFilter = function() {
		$scope.$close({
				dateFrom: "",
				dateTo: "",
				timeFrom: "",
				timeTo: "",
				description: "",
				amountLessThan: "",
				amountMoreThan: "",
				comment: ""		
		});
	}

});

expenseTrackerControllers.controller('WeeklyReportCtrl', function($scope, $window, $http, ENV) {
	
	function expensesUrl(url) {
		return ENV.apiEndpoint + "/users/" + $scope.user.id + "/expenses" + url;
	}
	
	function augmentWithAverage(data) {
		angular.forEach(data, function(week) {
			week.daily_average = week.sum_amount / 7.0;
		});
		return data;
	}
	
	var reportConfig = {
			params : {
				ftn: 'sum',
				field: 'amount',
				grouping: 'week'
			}
	};
	
	$http.get(expensesUrl('/aggregates'), reportConfig)
	.success(function(data) {
		$scope.weeks = augmentWithAverage(data);
	});
	
	$scope.print = function() {
		$window.print();
	};
});