var app = angular.module('ToughDoc', []);

app.controller('appCtrl', function($scope){
    $scope.ws = new WebSocket("ws://localhost:9000/ws");
    $scope.ws.onmessage = function(event) {
        $scope.text = event.data;
    }


    $scope.insert = function(){
        $scope.ws.send("Insert "+ $scope.char+" at "+$scope.pos);
    };
    
    $scope.delete = function(){
        $scope.ws.send("Delete "+$scope.pos);
    };
});