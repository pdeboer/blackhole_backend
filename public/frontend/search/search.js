angular.module( 'pplibdataanalyzer_frontend.search', [
  'ui.router',
  'angular-storage',
  'angular-jwt'
])
.config(function($stateProvider) {
  $stateProvider.state('search', {
    url: '/search',
    controller: 'SearchCtrl',
    templateUrl: 'search/search.html',
    data: {
      requiresLogin: false
    }
  });
})
.controller( 'SearchCtrl', function HomeController( $scope, $http, store, jwtHelper) {

  $scope.jwt = store.get('jwt');
  $scope.decodedJwt = $scope.jwt && jwtHelper.decodeToken($scope.jwt);

});
