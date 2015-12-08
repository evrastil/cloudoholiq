'use strict';

catalogApp.config(['$routeProvider',
    function ($routeProvider) {
        $routeProvider.
            when('/services', {
                templateUrl: 'app/components/offering/offering-list.html',
                controller: 'OfferingController',
                reloadOnSearch: false
            }).
            when('/services/:serviceId', {
                templateUrl: 'app/components/offering/offering-detail.html',
                controller: 'DetailOfferingController'
            });

    }]);

catalogControllers.controller('DetailOfferingController', function ($scope, $location, $routeParams, ServiceOfferingResource) {
        $scope.filterGroups = ServiceOfferingResource.query({serviceId: $routeParams.serviceId});
    }
);

catalogControllers.controller('OfferingController', function ($scope, $location, $routeParams, ServiceOfferingResource, FilterResource, CategoryResource, VendorResource) {

        function fetchServices(params) {
            ServiceOfferingResource.query(params, function (records) {
                records.forEach(function(item) {
                    CategoryResource.get({'categoryId': item.category}, function(c){
                        item.categoryName = c.name;
                    });
                    VendorResource.get({'vendorId': item.vendor}, function(v){
                        item.vendorName = v.name;
                    });
                });
                $scope.offerings = records;
            });
        }

        fetchServices($routeParams);

        $scope.checkboxes = [];

        $scope.search = function (query) {
            $location.search().q = [];
            if (query != "undefined" && query != null) {
                $location.search().q.push('name-contains@' + query);
            }
            fetchServices($location.search());
        };

        $scope.modifyQuery = function(query){
            if(typeof $location.search().q == "undefined" || $location.search().q == null){
                $location.search().q = [];
            }
            if (!$.isArray($location.search().q)) {
                var val = $location.search().q;
                $location.search().q = [val];
            }
            if($scope.checkboxes[query]){
                $location.search().q.push(query);
            }else{
                var index = $location.search().q.indexOf(query);
                $location.search().q.splice(index, 1);
            }
            $location.search($location.search());
            fetchServices($location.search());
        };

        $scope.modifyFilter = function(filterKey){
            if(typeof $location.search().f == "undefined" || $location.search().f == null){
                $location.search().f = [];
            }

            if($scope.checkboxes[filterKey]){
                $location.search().f.push(filterKey);
            }else{
                var index = $location.search().f.indexOf(filterKey);
                $location.search().f.splice(index, 1);
            }
            $location.search($location.search());

            fetchServices($location.search());
        };

        $scope.categories = CategoryResource.query();

        $scope.filterGroupsWithItems = FilterResource.query();

    }
);

