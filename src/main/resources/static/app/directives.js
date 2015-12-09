'use strict';

/* Directives */

var catalogDirectives = angular.module('catalogDirectives', []);

var defaults = this.defaults = {
    activeClass: 'active',
    routeAttr: 'data-match-route',
    strict: true
};

catalogDirectives.directive('bsNavbar', function bsNavbar($window, $location) {

    return {
        restrict: 'A',
        link: function postLink(scope, element, attr, controller) {

            // Directive options
            var options = angular.copy(defaults);
            angular.forEach(Object.keys(defaults), function(key) {
                if(angular.isDefined(attr[key])) options[key] = attr[key];
            });

            // Watch for the $location
            scope.$watch(function() {

                return $location.path();

            }, function(newValue, oldValue) {

                var liElements = element[0].querySelectorAll('li[' + options.routeAttr + '],li > a[' + options.routeAttr + ']');

                angular.forEach(liElements, function(li) {

                    var liElement = angular.element(li);
                    var pattern = liElement.attr(options.routeAttr).replace('/', '\\/');
                    if(options.strict) {
                        pattern = '^' + pattern;
                    }
                    var regexp = new RegExp(pattern, ['i']);

                    if(regexp.test(newValue)) {
                        liElement.addClass(options.activeClass);
                    } else {
                        liElement.removeClass(options.activeClass);
                    }

                });

                // Close all other opened elements
                var op = $('#sidebar-nav').find('.open:not(.active)');
                op.children('.submenu').slideUp('fast');
                op.removeClass('open');
            });

        }

    };
});

function gd(year, day, month) {
    return new Date(year, month - 1, day).getTime();
}


function showTooltip(x, y, label, data) {
    $('<div id="flot-tooltip">' + '<b>' + label + ': </b><i>' + data + '</i>' + '</div>').css({
        top: y + 5,
        left: x + 20
    }).appendTo("body").fadeIn(200);
}


catalogDirectives.directive('showtab', function() {
    return {
        link: function (scope, element, attrs) {
            element.click(function(e) {
                e.preventDefault();
                $(element).tab('show');
            });
        }
    };
});

catalogDirectives.directive('gridlist', function() {
    return {
        link: function (scope, element, attrs) {
$(document).ready(function() {
    $('#list').click(function(event){event.preventDefault();$('#products .item').addClass('list-group-item');});
    $('#grid').click(function(event){event.preventDefault();$('#products .item').removeClass('list-group-item');
    $('#products .item').addClass('grid-group-item');});
});
        }
    };
});