angular.module( 'pplibdataanalyzer_frontend.login', [
  'ui.router',
  'angular-storage'
])
.config(function($stateProvider) {
  $stateProvider.state('login', {
    url: '/login',
    controller: 'LoginCtrl',
    templateUrl: 'login/login.html'
  });
})
.controller( 'LoginCtrl', function LoginController( $scope, $http, store, $state) {

  $scope.user = {};

  $scope.login = function() {
    $http({
      url: 'http://localhost:9000/users/getJWT',
      method: 'POST',
      data: $scope.user
    }).then(function(response) {
      store.set('jwt', response.data);
      $state.go('home');
    }, function(error) {
      alert(error.data);
    });
  }

});
