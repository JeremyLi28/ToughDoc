var app = angular.module('ToughDoc', []);

app.controller('appCtrl', function($scope){
    $scope.ws = new WebSocket("ws://localhost:9000/ws");
    $scope.ws.onmessage = function(event) {
        $scope.$apply(function() {
            $scope.text = event.data;
            console.log($scope.text);
        });
    };
    
    $scope.createDoc = function () {
        
    };

    $scope.joinDoc = function () {
        var query = (JSON.stringify({
            type: "JoinDoc",
            docId: $scope.join_doc_id
        }));
        $scope.ws.send(query);
    };
    
    $scope.leaveDoc = function () {
        var query = (JSON.stringify({
            type: "LeaveDoc",
            docId: $scope.leave_doc_id
        }));
        $scope.ws.send(query);
    };
    
    $scope.insert = function(){
        var query = (JSON.stringify({
            type: "Insert",
            character: $scope.char,
            position: $scope.insert_pos
        }));
        $scope.ws.send(query);
    };
    
    $scope.delete = function(){
        var query = (JSON.stringify({
            type: "Delete",
            position: $scope.delete_pos
        }));
        $scope.ws.send(query);
    };
});