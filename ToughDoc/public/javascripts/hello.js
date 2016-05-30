var app = angular.module('ToughDoc', []);

app.controller('appCtrl', function($scope){
    $scope.ws = new WebSocket("ws://localhost:9000/ws");
    $scope.ws.onmessage = function(event) {
        $scope.$apply(function() {
            $scope.text = event.data;
        });
    };
    
    $scope.createDoc = function () {
        
    };

    $scope.joinDoc = function () {
        var query = (JSON.stringify({
            type: "JoinDoc",
            docId: $scope.doc
        }));
        $scope.ws.send(query);
    };
    
    $scope.leaveDoc = function () {
        var query = (JSON.stringify({
            type: "LeaveDoc",
            docId: $scope.doc
        }));
        $scope.ws.send(query);
    };
    
    $scope.insert = function(){
        var query = (JSON.stringify({
            type: "Insert",
            char: $scope.char,
            pos: $scope.pos
        }));
        $scope.ws.send(query);
    };
    
    $scope.delete = function(){
        var query = (JSON.stringify({
            type: "Delete",
            pos: $scope.pos
        }));
        $scope.ws.send(query);
    };
});