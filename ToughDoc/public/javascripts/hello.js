var app = angular.module('ToughDoc', []);

app.controller('appCtrl', function($scope){
    $scope.ws = new WebSocket("ws://http://ec2-52-36-57-249.us-west-2.compute.amazonaws.com:9000/ws");
    $scope.text = "";

    String.prototype.insert = function(idx, str) {
        return this.slice(0, idx) + str + this.slice(idx);
    };

    String.prototype.delete = function(idx) {
        return this.slice(0, idx) + this.slice(idx+1);
    };

    $scope.ws.onmessage = function(event) {

        $scope.$apply(function() {
            var json = JSON.parse(event.data);
            switch (json.type) {
                case "Insert":
                    $scope.text = $scope.text.insert(json.position, json.character);
                    break;
                case "Delete":
                    $scope.text = $scope.text.delete(json.position);
                    break;
            }
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