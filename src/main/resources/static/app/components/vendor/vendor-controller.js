'use strict';

catalogApp.config(['$routeProvider',
    function ($routeProvider) {
        $routeProvider.
            when('/vendors', {
                templateUrl: 'app/components/vendor/vendor-list.html',
                controller: 'VendorController'
            });
    }]
);

catalogControllers.controller('VendorController',
    function ($scope, $location, VendorResource) {
        $scope.vendors = VendorResource.query();

    }
);