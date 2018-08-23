app.controller("searchController", function ($scope,$location, searchService) {

    buildPageLabel = function () {
        $scope.pageLabel = [];
        var maxPageNo = $scope.resultMap.totalPages;
        var firstPage=1;
        var lastPage=maxPageNo;
        $scope.firstDot=true;
        $scope.lastDot=true;
        if ($scope.resultMap.totalPages > 5) {
            if ($scope.resultMap.pageNo <= 3) {
                lastPage = 5;
                $scope.firstDot = false;
            } else if ($scope.resultMap.pageNo > lastPage - 2) {
                firstPage = maxPageNo - 4;
                $scope.lastDot = false;

            } else {
                firstPage = $scope.searchMap.pageNo - 2;
                lastPage = $scope.searchMap.pageNo + 2;
            }
        }else {
            //前面无点
            $scope.firstDot=false;
            //后边无点
            $scope.lastDot=false;
        }

        
        for(var i=firstPage;i<lastPage;i++){
            $scope.pageLabel.push(i);
        }

        
        $scope.isTopPage=function () {
           if( $scope.searchMap.pageNo==1){
               return true;
           }else{
               return false;
           }
        }

        $scope.isEndPage = function () {
            if ($scope.searchMap.pageNo == $scope.searchMap.totalPages) {
                return true;
            }else {
                return false;
            }

        };
    };

    $scope.queryByPage = function (pageNo) {
        if (pageNo < 1 || pageNo > $scope.resultMap.totalPages) {
            return ;
        }
        $scope.searchMap.pageNo=pageNo;
        $scope.search();
        isCurPage($scope.searchMap.pageNo);
    };


    $scope.search=function(){
        $scope.searchMap.pageNo = parseInt($scope.searchMap.pageNo);
        searchService.search($scope.searchMap).success(
            function (response) {
                $scope.resultMap=response;

            }
        );
   buildPageLabel();

    }

    $scope.searchMap={'keywords':'','category':'','brand':'','spec':{},
        'price':'','pageNo':1,'pageSize':40,'sortField':'', 'sort': ''};

    $scope.addSearchItem = function (key, value) {

        if(key=='category' || key=='brand' || key=='price'){
            $scope.searchMap[key]=value;
        }else{
            $scope.searchMap.spec[key] = value;
        }
        $scope.search();//执行搜索

    };

    $scope.isCurPage = function (curPage) {
        if (curPage == $scope.searchMap.pageNo) {
            return true;
        }else{
            return false;
        }
    };

    $scope.removeSearchItem = function (key) {
        if(key=="category" || key=="brand"|| key=='price'){
            $scope.searchMap[key]="";
        }else{
            delete $scope.searchMap.spec[key];
        }
        $scope.search();//执行搜索
    };

    $scope.sortSearch = function (sortField, sort) {
        $scope.searchMap.sortField=sortField;
        $scope.searchMap.sort=sort;
        $scope.search();

    };
    $scope.keywordsIsBrand = function () {
        for(var i=0;i<$scope.resultMap.brandList.length;i++){
            if ($scope.searchMap.keywords.indexOf($scope.resultMap.brandList[i].text) >= 0) {
                $scope.searchMap.brand=$scope.resultMap.brandList[i].text;
                return true;
            }
        }
        return false;

    };






    $scope.loadkeywords = function () {
        $scope.searchMap.keywords=$location.search()['keywords'];
        $scope.search();

    };


});