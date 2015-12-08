'use strict';
catalogApp.config(['$routeProvider',
        function ($routeProvider) {
            $routeProvider.
                when('/login', {
                    templateUrl: 'app/components/user/user-login.html',
                    controller: 'UserController'
                }).
                when('/user/profile', {
                    templateUrl: 'app/components/user/user-profile.html',
                    controller: 'UserController'
                });
        }]
);

catalogControllers.controller('UserController',
    function () {

    }
);