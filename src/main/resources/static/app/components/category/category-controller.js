'use strict';

catalogApp.config(['$routeProvider',
    function ($routeProvider) {
        $routeProvider.
            when('/categories', {
                templateUrl: 'app/components/category/category-list.html',
                controller: 'CategoryController'
            });
    }]
);

catalogControllers.controller('CategoryController',
    function ($scope, $location, CategoryResource, $resource) {

        var ServiceCount = $resource('api/services/:count', {count: 'count'});
        //var VendorCount = $resource('api/vendors/:count', {count: 'count'});

        CategoryResource.query(function (records) {
            angular.forEach(records, function (item) {
                ServiceCount.get({count: 'count', q: "category@" + item.id}, function (list) {
                    item.serviceCount = list.count;
                });
                //VendorCount.get({count: 'count', q: "category@" + item.id}, function (list) {
                //    item.vendorCount = list.count;
                //});
            });
            $scope.categories = records;
        });
    }
);