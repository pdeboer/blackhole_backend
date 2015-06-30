angular.module( 'pplibdataanalyzer_frontend.about', [
  'ui.router',
  'angular-storage',
  'angular-jwt'
])
.config(function($stateProvider) {
  $stateProvider.state('about', {
    url: '/about',
    controller: 'AboutCtrl',
    templateUrl: 'about/about.html',
    data: {
      requiresLogin: false
    }
  });
})
.controller( 'AboutCtrl', function HomeController( $scope, $http, store, jwtHelper) {

  $scope.jwt = store.get('jwt');
  $scope.decodedJwt = $scope.jwt && jwtHelper.decodeToken($scope.jwt);

});