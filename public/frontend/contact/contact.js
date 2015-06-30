angular.module( 'pplibdataanalyzer_frontend.contact', [
  'ui.router',
  'angular-storage',
  'angular-jwt'
])
.config(function($stateProvider) {
  $stateProvider.state('contact', {
    url: '/contact',
    controller: 'ContactCtrl',
    templateUrl: 'contact/contact.html',
    data: {
      requiresLogin: false
    }
  });
})
.controller( 'ContactCtrl', function HomeController( $scope, $http, store, jwtHelper) {

  $scope.jwt = store.get('jwt');
  $scope.decodedJwt = $scope.jwt && jwtHelper.decodeToken($scope.jwt);

});