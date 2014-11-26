/**
 * 分组管理功能模块
 * 1、选定组进行门店的分配移除
 * 2、新增分组
 * 3、删除分组
 * 
 */
;(function($, M) {
	
	var root = this,
	document = root.document,
	GroupManagement,
	DropList,

	CHECKBOX_SELECTED = 'selected',

	CATEGORY = ["区域组", "级别组","临时组"];


	// 重构下拉列表类，继承自MusicBase.DropList
    DropList = M.inherit(M.DropList, {
        constructor : DropList,
        getListDom : function(data) {
            var i = 0, 
            l = data.length,
            _str = [];

            for (; i < l; i++) {
                var item = data[i];
                i === 0 && (this.current = item);
                _str.push('<li>' + item + '</li>');
            }

            this.list = _str.join('');
        }
    });


	GroupManagement = function() {
		var _default = {

			// APIS
			// 获取某一分类下的所有分组
			getGroupsUrl : _Url.getGroupsByCategory,
			// 创建组
			createGroupUrl : _Url.createGroup,
			// 获取未分配的门店
			getUnAssignedStoresUrl : _Url.getUnAssignedStores,

			// 获取某一分组下的所有门店
			getAllStoresInGroupUrl : _Url.getAllStoresInGroup,

			// 删除分组
			deleteGroupsUrl : _Url.deleteGroups,

			// 将门店添加到某个组
			addStoresToGroupUrl : _Url.addStoresToGroup,

			// 从某组删除门店
			removeStoresFormGroupUrl : _Url.removeStoresFormGroup,


			// DOMS
			wrapper 			: $('#J_Wrapper'),
			groupsList 			: $('#J_Group_List'),
			leftStoresList 		: $("#J_LStores_List"),
			rightStoresList 	: $("#J_RStores_List"),
			leftStoresListBody 	: $("#J_LStores_List_Body"),
			rightStoresListBody : $("#J_RStores_List_Body"),
			addGroupBtn 		: $("#J_Add_Group"),
			deleteGroupBtn 		: $("#J_Delete_Group"),
			assignedBtn 		: $("#J_Assigned_Btn"),
			unAssignedBtn 		: $("#J_Unassigned_Btn"),
			confirmedBtn 		: $("#J_Confirmed_Btn"),


			// private data
			_assignArray : [],
			_unassignArray : []
		}

		$.extend(this, _default);
		this.init.apply(this, arguments);
	};

	GroupManagement.prototype = {
		constructor : GroupManagement,
		init : function() {

			// 初始化页面数据
			this.initData();

			// 初始化交互
			this.initInterAction();

			// 事件绑定
			this.bindEvents();
		},

		initData : function() {
			this.getUnAssignedStores(function(data) {
				this.buildStoresList(this.leftStoresListBody, data);
			});
			this.getSpecificGroups(root.config.category, function(data) {
				this.buildGroupsList(data);
			});
		},

		checkClick : function(obj, options) {
			var toSelectAll = options && options.toSelectAll,
				toCancelAll = options && options.toCancelAll;

			if (obj.hasClass(CHECKBOX_SELECTED)) {
				obj.removeClass(CHECKBOX_SELECTED);
				options && options.toCancelAll && $.isFunction(options.toCancelAll) && options.toCancelAll();
			} else {
				obj.addClass(CHECKBOX_SELECTED);
				options && options.toSelectAll && $.isFunction(options.toSelectAll) && options.toSelectAll();
			}
		},

		initInterAction : function() {
			var _this = this,
				_currentE = this.groupsList.find('.lh-r span'),
				_list = this.groupsList.find('ul'),
				_checkClick = this.checkClick;

			// 组别下拉列表交互
			this.groupsList.delegate('.list-item', 'click', function(e) {
				e.preventDefault();
				_list.toggle();
			});

			_list.delegate('li', 'mousedown', function(e) {
				var ele = $(this);
				e.preventDefault();
				
				_this.groupsListSelect($(this).index(), function() {
					_list.hide();
				});
				
			});


			// 复选框点击选择功能
			this.wrapper.delegate('.list-body .list-item', 'mousedown', function(e) {
				e.preventDefault();
				_checkClick($(this));
			});

			this.leftStoresList.delegate('.list-head .list-item', 'mousedown', function(e) {
				e.preventDefault();
				_checkClick($(this), {
					toSelectAll : function() {
						_this.leftStoresListBody.find('.list-item').addClass(CHECKBOX_SELECTED);
					},
					toCancelAll : function() {
						_this.leftStoresListBody.find('.list-item').removeClass(CHECKBOX_SELECTED);
					}
				});
			});

			// 门店分配与取消分配
			// 
			var getSelectedItem = function(container, isDelete) {
				var _assignArray = (function() {
					var
					_array = [], 
					_element = container.find("." + CHECKBOX_SELECTED + " span");
					_element.each(function(index, value) {
						//_array.push('"' + value.innerHTML + '"');
						_array.push(value.innerHTML);
					});

					isDelete && container.find("." + CHECKBOX_SELECTED).remove() && _this.renderBody(container); 

					return _array;

				})();

				return _assignArray;
			};

			this.assignedBtn.bind("mousedown", function() {
				_this.assignedStores(getSelectedItem(_this.leftStoresListBody, true));
			});

			this.unAssignedBtn.bind("mousedown", function() {
				_this.unAssignedStores(getSelectedItem(_this.rightStoresListBody, true));
			});

			this.confirmedBtn.bind("mousedown", function() {
				_this.confirmAssignStores();
			});

		},

		bindEvents : function() {
			var _this = this;
			this.addGroupBtn.bind("click", function() {
				_this.addGroups();
			});

			this.deleteGroupBtn.bind("click", function() {
				_this.deleteGroups();
			});
		},

		// 获取每次操作数组
		// this._assignArray为所有操作完成后，需要分配的门店
		// _appendArray 为每次分配操作结束，需要分配的门店
		/*getAppendArray : function(arr1, arr) {
			var _tempArray = M.distinct(arr1.concat(arr)),
			_tempArray2 = [].concat(_tempArray);
			_appendArray = (function() {
				var l = arr1.length;
					i = 0;
				for (; i < l; i++) {
					_tempArray.splice($.inArray(arr1[i], _tempArray), 1);
				}

				return _tempArray;
			})();

			return {
				appendArray : _appendArray,
				assginArray : _tempArray2
			};
		},*/

		// 分配门店
		assignedStores : function(arr) {
			this._assignArray = M.distinct(this._assignArray.concat(arr));
			this.appendItem(arr, this.rightStoresListBody);
		},

		// 将门店置入未分配组
		unAssignedStores : function(arr) {	
			this._unassignArray = M.distinct(this._unassignArray.concat(arr));
			this.appendItem(arr, this.leftStoresListBody);
		},

		// 操作汇总处理
		// 如果一个门店既出现在assignArray又出现在unassignArray中，则认为该门店无需操作
		// 否则将assignArray的数据添加到当前组中
		// 将unassignArray的数据从指定组中删除
		confirmAssignStores : function() {
			var _assignArray = this._assignArray,
				_assignArray2 = [].concat(_assignArray),
				_unassignArray = this._unassignArray,
				_unassignArray2 = [].concat(_unassignArray),
				assign_len = _assignArray.length,
				unassign_len = _unassignArray.length,
				new_assignArray = [],
				new_unassignArray = [],
				item,
				index;

			for (var i = 0; i < unassign_len; i++) {
				item = _unassignArray2[i];
				index = $.inArray(item, _assignArray);
				index !== -1 && _assignArray.splice(index, 1);
			}

			for (var i = 0; i < assign_len; i++) {
				item = _assignArray2[i];
				index = $.inArray(item, _unassignArray);
				index !== -1 && _unassignArray.splice(index, 1);
			}

			this.count = 0;
			this.limit = (_assignArray.length > 0 ? 1 : 0) + (_unassignArray.length > 0 ? 1 : 0);
			this.uploadData(_assignArray, this.addStoresToGroupUrl);
			this.uploadData(_unassignArray, this.removeStoresFormGroupUrl);
		},

		uploadData : function(arr, url) {
			var _this = this;
			if (arr.length > 0) {
				$.ajax({
	                type: 'POST',
	                url: url,
	                data: {
	                	storeName : arr,
	                	groupName : _this.currentGroup
	                },
	                contentType: 'application/x-www-form-urlencoded; charset=UTF-8',
	                dataType: "text",
	                success: function(data) {
	                	_this.count++;
	                	if (_this.count >= _this.limit) {
	                		_this.successAssigned(data);
	                	}
	                },
	                error : function(xhr, error, msg) {
	                    alert(msg);
	                }
	            });
			}
		},

		successAssigned : function() {
			this._assignArray = [];
			this._unassignArray = [];
			alert("提交成功");
		},

		// 门店分配和取消分配的前端行为
		appendItem : function(arr, obj) {
			var baseIdx = obj.find('.list-item').length,
				// itemClass = ['odd', 'even'],
				l = arr.length,
				str = '',
				i = 0,
				item;

			for (; i < l; i++) {
				item = arr[i].replace(/\"/gi,"");
				str += ['<div class="list-item">',
                    '<div class="lh-l"><i class="j-checkbox"></i></div>',
                    '<div class="lh-r"><span>' + item + '</span></div>',
                '</div>'].join("");
			}

			obj.append($(str));
			this.renderBody(obj);
		},

		// 添加新分组
		addGroups : function() {

            var _this = this;

            //若不存在弹出框，则实例化，否则直接调用本地的弹出框实例化
            if (!this.outBox) {
                var _prototype = {
                    initialize : function() {
                        var it = this;
                        this.ibody = this.box.find('.out-box-body');
                        this.getDomTemplate(function(_html, list) {

                            this.ibody.html(_html);
                            this.ibody.find('.J_List_Wrapper').append(list.widget);

                     		var groupElements = this.box.find('input[name="groupName"]');
                            // 事件绑定
                            this.box.delegate('.blue-button', 'mousedown', function() {
                                it.executeAdding({
                                	groupName : $.trim(groupElements.val()),
                                	groupCategory : list.current,
                                	expireDate : null
                                });
                            });

                            // 直接显示弹出层
                            this.show();
                        });
                    },
                    executeAdding : function(data) {
                        var it = this;
                        $.ajax({
                            type: 'POST',
                            url: _this.createGroupUrl,
                            data: data,
                            dataType: "text",
                            success: function(data) {
                            	// TODO
                            	// 添加分组成功后，如果与当前分类标准相同，是否需要立即添加到当前页面
                            	// 
                            	// 
                                it.hide();
                            },
                            error : function(xhr, error, msg) {
                                alert(xhr.responseText);
                            }
                        });
                    },
                    getDomTemplate : function(callback) {
                        var it = this;
                       		_html = ['<dl class="clearfix">',
                                '<dt>分组名称</dt>',
                                '<dd><input type="text" name="groupName" value="" /></dd>',
                                '<dt>分组类型</dt>',
                                '<dd class="J_List_Wrapper"></dd>',
                            '</dl>'].join('');

                        $.isFunction(callback) && callback.call(it, _html, new DropList(CATEGORY));
                    }     
                };

                this.outBox = (new (M.inherit( M.PopupBox, _prototype, { title : '添加分组' } )));

            } else {
                this.outBox.show();
            }
        },

        // 删除分组
        deleteGroups : function() {
			var _this = this;

            //若不存在弹出框，则实例化，否则直接调用本地的弹出框实例化
            if (!this.deleteBox) {
                var _prototype = {
                    initialize : function() {
                        var it = this;
                        this.ibody = this.box.find('.out-box-body');
                        this.getDomTemplate(function(_html, list) {

                            this.ibody.html(_html);
                            
                            // 事件绑定
                            this.box.delegate('.blue-button', 'mousedown', function() {
								var labels = it.box.find('li.' + CHECKBOX_SELECTED + ' label'),
									groups = [];

								labels.each(function(index, value) {
									groups.push('"' + value.innerHTML + '"');
								});
								
								it.executeDelete("[" + groups.join(",") + "]");
                            });

                            // 复选框事件
                            this.box.delegate('li', 'mousedown', function() {
								_this.checkClick($(this));
                            });

                            // 直接显示弹出层
                            this.show();
                        });
                    },
                    executeDelete : function(data) {
                        var it = this;
                        $.ajax({
                            type: 'POST',
                            url: _this.deleteGroupsUrl,
                            data: data,
                            contentType: "application/json",
                            dataType: "text",
                            success: function() {
                            	_this.getSpecificGroups(root.config.category, function(data) {
									this.buildGroupsList(data);
								});
                                it.hide();
                            },
                            error : function(xhr, error, msg) {
                                //alert(msg);
                            }
                        });
                    },
                    getDomTemplate : function(callback) {
                        var it = this;
                        _this.getSpecificGroups(root.config.category ,function(data) {
							var _html = [],
								_str = '',
								l = data.length, 
								i = 0,
								_item;

							for (; i < l; i++) {
								_item = data[i];
								_str += '<li><i class="j-checkbox"></i><label>' + _item["groupName"] + '</label></li>';
							}

                        	_html = '<div class="groups-list-wrap"><ul>' + _str + '</ul></div>';
                            
                            $.isFunction(callback) && callback.call(it, _html);
                        });
                    }     
                };

                this.deleteBox = (new (M.inherit( M.PopupBox, _prototype, { title : '删除分组' } )));

            } else {
                this.deleteBox.show();
            }
        },

		groupsListSelect : function(index, callback) {
			var _this = this,
				_selectedClass = 'selected',
				_currentE = this.groupsList.find('.lh-r span'),
				_list = this.groupsList.find('ul'),
				_litem = _list.find('li'),
				_nowli = $(_litem[index]);

			this._temp && this._temp.removeClass(_selectedClass);
			_nowli.addClass(_selectedClass);

			this.currentGroup = _nowli.html();
			_currentE.html(this.currentGroup);

			this._temp = _nowli;

			$.ajax({
                type : "GET",
                url : _this.getAllStoresInGroupUrl.replace(/\{groupName\}/gi, this.currentGroup),
                data : null,
                dataType : "json",
                success : function(data) {
               		_this.buildStoresList(_this.rightStoresListBody, data);
               		$.isFunction(callback) && callback.call(_this, data);
                },
                error : function(xhr, error, msg) {
                    alert(error);
                }
            });
		},

		// 获取未分配组数据
		getUnAssignedStores : function(callback) {
			var _this = this,
				_url = this.getUnAssignedStoresUrl;


			$.ajax({
                type : "GET",
                url : _url,
                data : null,
                dataType : "json",
                success : function(data) {
                    $.isFunction(callback) && callback.call(_this, data);
                },
                error : function(xhr, error, msg) {
                    alert(error);
                }
            });
		},

		// 获取指定分类下的所有组别
		getSpecificGroups : function(category, callback) {
			var 
			_this = this;
			_url = this.getGroupsUrl.replace(/\{category\}/gi, category);

			$.ajax({
                type : "GET",
                url : _url,
                data : null,
                dataType : "json",
                success : function(data) {
                    $.isFunction(callback) && callback.call(_this, data);
                },
                error : function() {
                   alert(arguments);
                }
            });
		},

		// 构造分组下拉列表
		buildGroupsList : function(data) {
			var l = data.length;
			if (l <= 0) return;
			var	i = 0,
				_current = data[0].groupName,
				_item,
				_str = '';

			for (; i < l; i++) {
				_item = data[i],
				_str += '<li>' + _item.groupName + '</li>';
			}

			this.groupsList.find('.lh-r span').html(_current);
			this.currentGroup = _current;
			this.groupsList.find('.drop-list').html(_str);

			this.groupsListSelect(0);
		},

		// 构造门店列表
		buildStoresList : function(obj, data) {
			var _this = this,
				itemClass = ["odd", "even"],
				l = data.length,
				i = 0,
				str = '',
				item;

			for (; i < l; i++) {
				item = data[i];
				str += 
				['<div class="list-item ' + itemClass[i%2] + '">',
                    '<div class="lh-l"><i class="j-checkbox"></i></div>',
                    '<div class="lh-r"><span>' + item["storeName"] + '</span></div>',
                '</div>'].join("");
			}

			obj.html(str);
		},

		renderBody : function(obj) {
			var items = obj.find('.list-item'),
				l = items.length,
				i = 0,
				item;

			for (; i < l; i++) {
				item = $(items[i]);
				i%2 === 0 
				? item.removeClass('even').addClass('odd') 
				: item.removeClass('odd').addClass('even');
			}
		}
	};

	root["GroupManagement"] = GroupManagement;

}).call(this, jQuery, MusicBase);


$(function() {
	window.groupManagement = new GroupManagement();
});