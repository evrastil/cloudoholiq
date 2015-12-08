var catalogApp = angular.module('catalogApp', [
	'ngRoute',
	'ngResource',
	'catalogControllers',
	'catalogDirectives',
	'catalogResources'
]);


/**
 * Configure the Routes
 */
catalogApp.config(['$routeProvider', function ($routeProvider) {
	$routeProvider
		.when("/", {
			redirectTo:'/dashboard'
		})
		.when("/error-404", {
			templateUrl: "error-404-v2.html",
			controller: "MainController"
		})
		.when("/error", {
			templateUrl: "error-500.html",
			controller: "MainController"
		})
		.otherwise({
			redirectTo:'/error-404'
		});
}]);

catalogApp.controller('MainController', function () {

	}
);


catalogApp.run(['$location', '$rootScope', function($location, $rootScope) {

}]);