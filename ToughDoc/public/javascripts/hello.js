var app = angular.module('ToughDoc', []);

app.controller('appCtrl', function($scope){
    $scope.ws = new WebSocket("ws://localhost:9000/ws");
    $scope.text = "";
    $scope.userId = -1;
    $scope.delay = 0;
    $scope.logs = []

    String.prototype.insert = function(idx, str) {
        return this.slice(0, idx) + str + this.slice(idx);
    };

    String.prototype.delete = function(idx) {
        return this.slice(0, idx) + this.slice(idx+1);
    };

    $scope.ws.onmessage = function(event) {

        $scope.$apply(function() {
            $scope.json = JSON.parse(event.data);
            switch ($scope.json.type) {
                case "Join":
                {
                    $scope.userId = $scope.json.userId;
                    break;
                }
                case "Insert":
                {
                    if($scope.userId != $scope.json.userId) {
                        $scope.text = $scope.text.insert($scope.json.position, $scope.json.character);
                        $scope.logs.push("User"+$scope.json.userId+": Insert " + $scope.json.character + " at " + $scope.json.position)
                    }
                    break;
                }
                case "Delete":
                {
                    if($scope.userId != $scope.json.userId) {
                        $scope.text = $scope.text.delete($scope.json.position);
                        $scope.logs.push("User"+$scope.json.userId+": Delete at " + $scope.json.position)
                    }
                    break;
                }
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
    
    $scope.insert = function(char, insert_pos){
        var query = (JSON.stringify({
            type: "Insert",
            character: char,
            position: insert_pos
        }));
        $scope.ws.send(query);
    };
    
    $scope.delete = function(delete_pos){
        var query = (JSON.stringify({
            type: "Delete",
            position: delete_pos
        }));
        $scope.ws.send(query);
    };

    $scope.$watch('text', function (newVal, oldVal) {
        var i = 0;
        if(!newVal)
          return;
        if($scope.userId != $scope.json.userId) {
            $scope.json.userId = $scope.userId;
            return;
        }
        if(newVal.length > oldVal.length) {
            for (i = 0; i < oldVal.length; i++) {
                if(newVal[i] != oldVal[i]) {
                    break;
                }
            }
            console.log("Insert " + newVal[i] + " at " + i);
            $scope.logs.push("User"+$scope.userId+": Insert " + newVal[i] + " at " + i)
            setTimeout(function () {
                $scope.insert(newVal[i], i);
            }, $scope.delay*1000);

        }
        else if (newVal.length < oldVal.length) {
            for (i = 0; i < newVal.length; i++) {
                if(newVal[i] != oldVal[i]) {
                    break;
                }
            }
            console.log("Delete " + oldVal[i] + " at " + i);
            $scope.logs.push("User"+$scope.userId+": Delete at " + i)
            setTimeout(function () {
                $scope.delete(i);
            }, $scope.delay*1000);

        }
        // else {
        //     for (i = 0; i < newVal.length; i++) {
        //         if(newVal[i] != oldVal[i]) {
        //             break;
        //         }
        //     }
        //     console.log("Update " + oldVal[i] + " at " + i + " to "+newVal[i]);
        //     $scope.delete(i);
        //     $scope.insert(newVal[i], i);
        // }
    })


});