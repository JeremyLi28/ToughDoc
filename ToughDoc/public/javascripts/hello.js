var app = angular.module('ToughDoc', []);

app.controller('appCtrl', function($scope){
    $scope.insert = function(){
        console.log("Insert "+ $scope.char+" at "+$scope.pos);
    };
    
    $scope.delete = function(){
        console.log("Delete "+$scope.pos);
    };
});