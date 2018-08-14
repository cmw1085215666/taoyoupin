app.service('brandService' ,function($http){
    //读取列表数据绑定到表单中
    this.findAll=function(){
        return $http.get('../brand/findAll.do');
    };


    this.reLoadList=function(currentPage,itemsPerPage,searchEntity){
        return this.search(currentPage,itemsPerPage,searchEntity);
    };



    this.findPage=function(page,rows) {
        return $http.get('../brand/findPage.do?page=' + page + '&rows=' + rows);
    };


    this.search=function(page,rows,searchEntity) {
        return $http.post('../brand/search.do?page=' + page + '&rows=' + rows,/*$scope.*/searchEntity);
    };

    this.save=function(methodName,entity) {


        return $http.post('../brand/'+methodName+'.do?',entity);
    };


    this.findOne=function(id){
        return $http.get('../brand/findOne.do?id='+id);
    };




    this.dele=function(selectIds){
        //获取选中的复选框
        return $http.get('../brand/delete.do?ids='+/*$scope.*/selectIds);
    };

});