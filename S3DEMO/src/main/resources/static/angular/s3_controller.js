app.controller("s3Ctrl",function($scope,$http) {
    
$scope.requestDto={};

$scope.isUpload=true;

$scope.uploadFile=function(){

	$scope.isUpload=true;
	
	var reader = new FileReader();
	reader.onload = function(e) {
		$scope.requestDto.fileString = (reader.result).replace("data:image/jpeg;base64", "");
		$scope.requestDto.fileName = $scope.myFile.name;
		$scope.callApi();
	}
	reader.readAsDataURL($scope.myFile);

}

$scope.callApi=function(){
	if($scope.validateInput()){
		$http({
			  method:'POST',
			  url: 'http://localhost:8081/s3demo/api/v1/save',
			  data: $scope.requestDto,
		}).then(function successCallback(response) {
			console.log(response);
			if(response.data!=undefined &&  response.data.status== "Success"){
				alert("Upload successfully..Thanks :)")
				$scope.myFile = "";
			}else{
				alert("Oops..some thing missing");
			}
		}, function errorCallback(response) {
			alert("Oops..some thing missing");
		});
	}
}
	
$scope.downloadFile=function(){

	$scope.isUpload=false;
	
	if($scope.validateInput()){
		
		$http({
			  method: 'GET',
			  url: '/someUrl'
			}).then(function successCallback(response) {
				
    			
			},function errorCallback(response) {

				
				
			});
	}
}

$scope.validateInput=function(){
	
	if($scope.isUpload){
		if($scope.requestDto.fileString == undefined || $scope.requestDto.fileString == null || $scope.requestDto.fileString == ""){
			alert("please choose file and upload");
		    return false;
		}
	}else if($scope.requestDto.fileName== undefined || $scope.requestDto.fileName == null || $scope.requestDto.fileName == ""){
		alert("please enter file name and download");
		return false;
	}
	return true;
}
	
});