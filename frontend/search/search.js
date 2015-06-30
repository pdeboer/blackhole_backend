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


                  $http.get("http://localhost:9000/coordinates").
                          success(function (data, status, headers, config) {
                              $scope.coordinates = data;
                              $scope.uri = "http://skyservice.pha.jhu.edu/DR10/ImgCutout/getjpeg.aspx?ra=" + data.ra + "&dec=" + data.dec + "&scale=0.050806&width=1536&height=1536&opt=";

                          }).
                          error(function (data, status, headers, config) {
                              alert("error");
                              // called asynchronously if an error occurs
                              // or server returns response with an error status.
                          });




});
