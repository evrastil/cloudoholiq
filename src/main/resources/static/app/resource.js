'use strict';

var catalogResources = angular.module('catalogResources', ['ngResource']);

catalogResources.factory('CategoryResource', ['$resource', function ($resource) {
    return $resource('/api/categories/:categoryId', {},
        {
            update: {method: 'PUT'}
        });
}]);

catalogResources.factory('ServiceOfferingResource', ['$resource', function ($resource) {
    return $resource('/api/services/:serviceId', {},
        {
            update: {method: 'PUT'}
        });
}]);

catalogResources.factory('PropertyResource', ['$resource', function ($resource) {
    return $resource('/api/services/:service/properties/:propertyId', {},
        {
            update: {method: 'PUT'}
        });
}]);

catalogResources.factory('VendorResource', ['$resource', function ($resource) {
    return $resource('/api/vendors/:vendorId', {},
        {
            update: {method: 'PUT'}
        });
}]);

catalogResources.factory('FilterResource', ['$resource', function ($resource) {
    return $resource('/api/filters/:filterId', {},
        {
            update: {method: 'PUT'}
        });
}]);

catalogResources.factory('OptionGroupResource', ['$resource', function ($resource) {
    return $resource('/api/catalogs/:catalogId/items/:itemId/option-groups', {},
        {
            update: {method: 'PUT'}
        });
}]);