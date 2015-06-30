var app = angular.module("search").controller("MainSearch",function($scope,$http){
$http.get("http://localhost:9000/tasks/0").
  success(function(data, status, headers, config) {
  $scope.task = data;
  alert($scope.task);
    // this callback will be called asynchronously
    // when the response is available
  }).
  error(function(data, status, headers, config) {
  alert("error");
    // called asynchronously if an error occurs
    // or server returns response with an error status.
  });
    });
    



//