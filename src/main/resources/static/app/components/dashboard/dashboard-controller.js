'use strict';

catalogApp.config(['$routeProvider',
    function ($routeProvider) {
        $routeProvider.
            when('/dashboard', {
                templateUrl: 'app/components/dashboard/dashboard-index.html',
                controller: 'DashboardController'
            });
    }]);

catalogControllers.controller('DashboardController',
    function () {

    }
);