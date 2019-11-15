src="http://api.map.baidu.com/api?v=2.0&ak=6jUA5PKDKmyb1D0ipI1iC1onLYPlRB7I";

function getData(ori,des){
	// 百度地图API功能
	var map = new BMap.Map("allmap");
	map.centerAndZoom(new BMap.Point(116.404, 39.915), 12);
	var output = "从上地到西单坐公交需要";
	var searchComplete = function (results){
		if (transit.getStatus() != BMAP_STATUS_SUCCESS){
			return ;
		}
			var plan = results.getPlan(0);
			output += plan.getDuration(true) + "\n";  //获取时间
			output += plan.getDistance(true) + "\n";  //获取距离

	}
	var transit = new BMap.TransitRoute(map, {renderOptions: {map: map},
		onSearchComplete: searchComplete,
		onPolylinesSet: function(){
			setTimeout(function(){alert(output)},"1000");
		}});
    var start=new BMap.Point(116.404844,39.911836);
    var end=new BMap.Point(116.308102,40.056057);
    transit.search(start, end);
    return output;
}