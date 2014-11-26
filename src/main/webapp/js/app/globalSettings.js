;(function() {

	var baseURL = "../../../webadmin/";
	var adminRestURL = baseURL + "adminrest/";

	this._Url = {
		// 登陆接口
		login : adminRestURL + "login",

		// 登出接口
		logout : adminRestURL + "logout",

		// android状态接口
		androidStatus : adminRestURL + "musicdevice/monitor",

		// 获取所有的曲目
		getAllMusics : adminRestURL + "home/playlist",

		// 上传MP3接口
		uploadMp3 : adminRestURL + "home/edit/upload",

		// 删除或增加曲目至总列表
		addAndRemoveMusics : adminRestURL + "home/playlist/update",

		// 增加曲目至指定组
		addMusicsToSpecial : adminRestURL + "home/playlist/group/{groupName}/add",

		// 从指定组删除曲目
		removeMusicsFromSpecial : adminRestURL + "home/playlist/group/{groupName}/remove",

		// 获取所有组类
		getAllGroups : adminRestURL + "home/groups",

		// 按分类标准获取所有组
		getGroupsByCategory : adminRestURL + "home/groups/category/{category}",

		// 获取未分配的门店
		getUnAssignedStores : adminRestURL + "home/store/unassigned",

		// 获取某一分组下的所有门店
		getAllStoresInGroup : adminRestURL + "home/store/{groupName}",

		// 获取指定组别下的所有曲目
		getMusicsOfOneGroup : adminRestURL + "home/playlist/group/{groupName}",

		// 通过id获取组
		getGroupById : adminRestURL + "home/groups/id/{id}",

		// 创建组
		createGroup : adminRestURL + "home/groups/create",

		// 按照id删除组
		deleteGroupById : adminRestURL + "home/groups/id/{id}",

		// 按组名删除分组
		deleteGroups : adminRestURL + "home/groups/delete",

		// 将一系列分店添加到某个组
		addStoresToGroup : adminRestURL + "home/store/add/to/group",

		// 从某个分组中删除一系列分店
		removeStoresFormGroup : adminRestURL + "home/store/remove/from/group"

	};

	// Test api
	// 
	return false;
	
	var _baseUrl = "../../testAPI/";

	this._Url = {
		getAllMusics : _baseUrl + "getAllMusics.txt",
		addAndRemoveMusics : _baseUrl + "addAndRemoveMusics.txt",

		getAllGroups : _baseUrl + "groups.txt",
		androidStatus : _baseUrl + "monitor.txt",

		addStoresToGroup : _baseUrl + "toGroup.txt",
		removeStoresFormGroup : _baseUrl + "removeStoresFormGroup.txt",
		getGroupsByCategory : _baseUrl + "getGroupsByCategory.txt",
		getMusicsOfOneGroup : _baseUrl + "getMusicsOfOneGroup.txt",

		uploadMp3 : _baseUrl + "uploadMp3.html",
		addMusicsToSpecial : _baseUrl + "addMusicsToSpecial.txt",
		removeMusicsFromSpecial : _baseUrl + "removeMusicsFromSpecial.txt",

		createGroup : _baseUrl + "createGroup.txt",
		deleteGroups : _baseUrl + "deleteGroups.txt",
		getUnAssignedStores : _baseUrl + "getUnAssignedStores.txt",
		getAllStoresInGroup : _baseUrl + "getAllStoresInGroup.txt"
	}

}).call(this);