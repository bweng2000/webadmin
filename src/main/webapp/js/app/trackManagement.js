/**
 * 曲目管理功能模块
 * 1、上传歌曲
 * 2、添加分组
 * 3、删除分组
 * 4、曲目上下移动
 */

;(function($, M) {

	var root = this,
	document = root.document,
    TrackManagement,
    DropList,

    LIST_DATA1 = ['每天', '每周'],
    LIST_DATA2 = ['17点', '16点'],
    LIST_DATA3 = ['25分', '30分'],

    // 头部滚动列表类
    ScrollList = function(options) {
        this.currentPage = 0;
        $.extend(this, options);
        this.init.apply(this, arguments);
    };
    ScrollList.prototype = {
        constructor : ScrollList,
        init : function() {
            
            // 所有列表项集合
            this.listItem = this.list.find('li');

            // 可视区域长度
            this.limitLength = this.list.parent().width();
            // 总列表项数目
            this.itemNumber = this.listItem.length;
            // 每个列表项长度
            this.listItemWidth = this.listItem.get(0).offsetWidth;
            // 所有列表项总长度
            this.totalWidth = this.listItemWidth * this.itemNumber;

            if (this.totalWidth > this.limitLength) {
                this.getScroll();
            } else {
                this.hideArrow();
            }
        },
        getScroll : function() {
            var _this = this;
            this.list.css("width", "50000px");
            this.pages = Math.ceil(this.totalWidth / this.limitLength);
            this.goTo(this.currentPage);

            this.leftArrow.bind("mousedown", function() {
                _this.goPrevious();
            });
            this.rightArrow.bind("mousedown", function() {
                _this.goNext();
            }); 
        },
        goPrevious : function() {
            this.goTo(this.currentPage - 1);
        },
        goNext : function() {
            this.goTo(this.currentPage + 1);
        },
        goTo : function(page) {
            var _this = this;
            if (page <= 0) {
                this.getBeginStatus();
                this.currentPage = 0;
            } else if (page >= this.pages - 1) {
                this.getEndStatus();
                this.currentPage = this.pages - 1;
            } else {
                this.showArrow();
                this.currentPage = page;
            }

            this.list.animate({
                "margin-left" : -_this.currentPage * _this.limitLength
            });

        },
        getBeginStatus : function() {
            this.leftArrow.hide();
            this.rightArrow.show();
        },
        getEndStatus : function() {
            this.leftArrow.show();
            this.rightArrow.hide();
        },
        hideArrow : function() {
            this.leftArrow.hide();
            this.rightArrow.hide();
        },
        showArrow : function() {
            this.leftArrow.show();
            this.rightArrow.show();
        }
    };

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


    // 曲目管理核心功能
	TrackManagement = M.inherit(M.BaseTable, {
		constructor : TrackManagement,
		initialize : function() {
			var _this = this;

            this.wrap = this.wrap || $("body", document);

            // 构造头部列表后获取默认数据
            this.buildTopList(function(_default) {

                this.scrollList = new ScrollList({
                    wrapper     : _this.topListWrapper,
                    list        : _this.topList,
                    leftArrow   : _this.topLeftArrow,
                    rightArrow  : _this.topRightArrow
                });

                this.getTableData(_default.groupName);

            });

            this.bindEvents();

		},

        bindEvents : function() {
            var _this = this;
            this.topList.delegate('li', 'click', function() {
                var groupName = $(this).find('a').html();
                if (groupName === _this.currentGroupName) {
                    return false;
                }
                _this.getTableData(groupName);
            });

            this.addMusicBtn.bind('click', function() {
                _this.addMusic();
            });

            this.deleteMusicBtn.bind('click', function() {
                _this.deleteMusic();
            });

            this.upMusicBtn.bind('click', function() {
                _this.upMusic();
            });

            this.downMusicBtn.bind('click', function() {
                _this.downMusic();
            });

            this.assignMusicBtn.bind('click', function() {
                _this.assignMusic();
            });

        },

        // 上移歌曲
        upMusic : function() {
            var currentRow = this._DataTable.rows(".selected"),
                oSettings = this._DataTable.rows().settings()[0],
                aoData = oSettings.aoData,
                aiDisplayMaster = oSettings.aiDisplayMaster,
                len = currentRow.nodes().length,
                index = currentRow.indexes()[0],
                tmpData = aoData[index],
                prevIndex = index - 1;
            

            if (len > 1) {
                alert('只能选取一行数据进行移动');
            } else if (len === 0 ) {
                alert('请选择需要进行移动的行');
            } else {
                if (prevIndex >= 0) {

                    // 移动当前行，修改dataTable内部数据结构aoData，进行置位，同时更改行索引_DT_RowIndex
                    aoData[index] = aoData[prevIndex];
                    aoData[index].nTr._DT_RowIndex = index;
                    aoData[prevIndex] = tmpData;
                    aoData[prevIndex].nTr._DT_RowIndex = prevIndex;


                    this._DataTable.draw();

                } else {
                    alert('已经移至顶行');
                }
            }
        },

        // 下移歌曲
        downMusic : function() {
            var currentRow = this._DataTable.rows(".selected"),
                oSettings = this._DataTable.rows().settings()[0],
                aoData = oSettings.aoData,
                aiDisplayMaster = oSettings.aiDisplayMaster,
                len = currentRow.nodes().length,
                index = currentRow.indexes()[0],
                tmpData = aoData[index],
                nextIndex = index + 1;
            

            if (len > 1) {
                alert('只能选取一行数据进行移动');
            } else if (len === 0 ) {
                alert('请选择需要进行移动的行');
            } else {
                if (nextIndex < aoData.length) {

                    aoData[index] = aoData[nextIndex];
                    aoData[index].nTr._DT_RowIndex = index;
                    aoData[nextIndex] = tmpData;
                    aoData[nextIndex].nTr._DT_RowIndex = nextIndex;


                    this._DataTable.draw();

                } else {
                    alert('已经移至末行');
                }
            }
        },

        // 添加歌曲
        addMusic : function() {
            var _this = this;

            //若不存在弹出框，则实例化，否则直接调用本地的弹出框实例化
            if (!this.outBox) {
                var _prototype = {
                    initialize : function() {
                        var it = this;
                        this.ibody = this.box.find('.out-box-body');
                        this.getDomTemplate(function(_html, list1, list2, list3) {
                            
                            this.ibody.html(_html);
                            this.ibody.find('.J_Select_Wrapper1').append(list1.widget);
                            this.ibody.find('.J_Select_Wrapper2').append(list2.widget);
                            this.ibody.find('.J_Select_Wrapper3').append(list3.widget);

                            this.ibody.find('input[type="file"]').bind('change', function() {
                                
                                var element = $(this),
                                    fileTriggerE = element.prev();
                                    fileNameE = element.next();

                                fileTriggerE.html('重新上传');
                                fileNameE.html(this.value);
                                fileNameE.attr('title', this.value);

                            });

                            // 事件绑定
                            this.box.delegate('.blue-button', 'mousedown', function() {
                                it.executeAdding(list1.current + list2.current + list3.current);
                            });

                            // 直接显示弹出层
                            this.show();
                        });
                    },
                    executeAdding : function(playtime) {
                        var it = this;
                            formE = it.box.find('form');

                        formE.find('[name="playtime"]').val(playtime);

                        root["uploadMp3callback"] = function(data) {
                            _this._DataTable.row.add({
                                "name" : 1,
                                "status" : 2,
                                "special" : false,
                                "playtime" : '2002'
                            }).draw();
                        
                            it.hide();
                        };

                        formE.submit();

                    },
                    getDomTemplate : function(callback) {
                        var it = this, _html = '';
                        _html = ['<form action="' + _this.uploadMp3 + '" target="J_Form_Target" method="POST" enctype="multipart/form-data">',
                                    '<dl class="clearfix">',
                                        '<dt>上传曲目</dt>',
                                        '<dd>',
                                            '<a href="javascript:;" class="red-button" onclick="this.parentNode.getElementsByTagName(\'input\')[0].click()">选择文件</a>',
                                            '<input type="file" name="file" value="" class="hide" />',
                                            '<span class="file-name"></span>',
                                        '</dd>',
                                        '<dt>播放时间</dt>',
                                        '<dd class="clearfix">',
                                            '<div class="j-select-wrapper J_Select_Wrapper1"></div>',
                                            '<div class="j-select-wrapper J_Select_Wrapper2"></div>',
                                            '<div class="j-select-wrapper no-mr J_Select_Wrapper3"></div>',
                                            '<input type="hidden" name="playtime" value="" />',
                                        '</dd>',
                                    '</dl>',
                                '</form>'].join('');

                        $.isFunction(callback) && callback.call(it, _html, new DropList(LIST_DATA1), new DropList(LIST_DATA2), new DropList(LIST_DATA3));
                    }    
                };

                this.outBox = (new (M.inherit( M.PopupBox, _prototype, { title : '添加歌曲' } )));

            } else {
                this.outBox.show();
            }
        },

        // 删除歌曲
        deleteMusic : function() {
            var _this = this,
            toDeleteTrs = this._DataTable.row('.selected');
            toDeleteTrNodes = toDeleteTrs.nodes();
            toDeleteArray = [],
            i = 0,
            l = toDeleteTrNodes.length;

        
            for (; i < l; i++) {
                var node = toDeleteTrNodes[i];
                toDeleteArray.push(node.getElementsByTagName('td')[1].innerHTML);
            }

            $.ajax({
                type : "PUT",
                url : _this.removeMusicsFromSpecial.replace(/\{groupName\}/gi, _this.currentGroupName),
                data : {
                    musics : "[" + toDeleteArray.join(',') + "]",
                },
                //dataType : "json",
                contentType: 'application/json',
                success : function(data) {
                    toDeleteTrs.remove().draw(false);
                },
                error : function() {
                    alert(arguments);
                }
            });

        },

        // 分配歌曲
        assignMusic : function() {
            var _this = this;

            //若不存在弹出框，则实例化，否则直接调用本地的弹出框实例化
            if (!this.outAssignBox) {
                var _prototype = {
                    initialize : function() {
                        var it = this;
                        this.ibody = this.box.find('.out-box-body');
                        this.getDomTemplate(function(_html, list) {

                            this.ibody.html(_html);
                            this.ibody.find('.J_List_Wrapper').append(list.widget);

                            // 事件绑定
                            this.box.delegate('.blue-button', 'mousedown', function() {
                                var toDeleteTrs = _this._DataTable.row('.selected');
                                    toDeleteTrNodes = toDeleteTrs.nodes();
                                    toDeleteArray = [],
                                    i = 0,
                                    l = toDeleteTrNodes.length;

                                if (l === 0) {
                                    alert("请选择需要分配曲目");
                                } else {

                                    for (; i < l; i++) {
                                        var node = toDeleteTrNodes[i];
                                        toDeleteArray.push(node.getElementsByTagName('td')[1].innerHTML);
                                    }

                                    it.executeAdding({
                                        musicNames : "[" + toDeleteArray.join() + "]"
                                    }, _this.addMusicsToSpecial.replace(/\{groupName\}/gi, list.current));
                                }
                            });

                            // 直接显示弹出层
                            this.show();
                        });
                    },
                    executeAdding : function(data, url) {
                        var it = this;
                        $.ajax({
                            type: 'GET',
                            url: url,
                            data: data,
                            dataType: "json",
                            success: function() {
                                // TODO
                                // 添加分组成功后，如果与当前分类标准相同，是否需要立即添加到当前页面
                                // 
                                // 
                                it.hide();
                            },
                            error : function(xhr, error, msg) {
                                alert(msg);
                            }
                        });
                    },
                    getDomTemplate : function(callback) {
                        var it = this,
                            _html;
                        _this.getGroups(function(data) {
                            _html = ['<dl class="clearfix">',
                                '<dt>选择分组</dt>',
                                '<dd class="J_List_Wrapper"></dd>',
                            '</dl>'].join('');

                            $.isFunction(callback) && callback.call(it, _html, new M.DropList(data));
                        });
                    }    
                };

                this.outAssignBox = (new (M.inherit( M.PopupBox, _prototype, { title : '添加到组' } )));

            } else {
                this.outAssignBox.show();
            }
        },

        // 根据特定组，获取表格数据
        getTableData : function(groupName) {

            this.tableSettings.ajax.url = this.musicOfGroup.replace(/\{groupName\}/gi, groupName);
            this.currentGroupName = groupName;
            // 表格已经初始化
            if ($.fn.dataTable.isDataTable(this.table)) {
                this.table.fnDestroy();
            }

            this.table.dataTable(this.tableSettings);
            this._DataTable = this.table.DataTable();
        },

        // 获取顶部各组数据，并构对应列表
        buildTopList : function(callback) { 
            var _this = this,
                _default = {};

            this.getGroups(function(data) {
                var l = data.length,
                    i = 0,
                    str = '',
                    item;

                for (; i < l; i++) {
                    item = data[i];
                    if (i === this.topList_defalut) {

                        _default.groupName = data[i]["groupName"];
                        _default.groupId = data[i]["id"];

                        str += '<li class="selected" group-id="' + _default.groupId + '" title="' + _default.groupName + '"><a href="javascript:;">' + _default.groupName + '</a></li>';

                    } else {
                        str += '<li group-id="' + data[i]["id"] + '" title="' + data[i]["groupName"] + '"><a href="javascript:;">' + data[i]["groupName"] + '</a></li>';
                    }
            
                }

                _this.topList.html(str);

                $.isFunction(callback) && callback.call(_this, _default);
            });
        },
        // 获取所有组别
        getGroups : function(callback) {
            var _this = this;
            $.ajax({
                type: 'GET',
                url: _this.groupsUrl,
                data: null,
                dataType: "json",
                success: function(data) {
                    $.isFunction(callback) && callback.call(_this, data);
                },
                error : function(xhr, error, msg) {
                    alert(msg);
                }
            });
        }



	}, {
		tableSettings : {
            "language": {
                "url": "../../assets/de_DE.txt"
            },
            "dom": 'lfrtip',
            "bProcessing" : true, 
            "bStateSave" : true, 
            "ajax" : {
                "url" : null,
                "dataSrc" : ""
            },
            "columns" : [
                null,
                { "data" : "name" },
                { "data" : "status" },
                { "data" : "special" },
                { "data" : "playtime" }
            ],
            "columnDefs" : [
                {
                    "targets": [0],
                    "orderable": false,
                    "defaultContent" : '<i class="j-checkbox"></i>'
                },
                {
                    "targets" : [1],
                    "sClass" : "left-dir"   
                }
            ]
        },

        // 获取特定分类下所有组别接口
        groupsUrl : _Url.getGroupsByCategory.replace(/\{category\}/gi, root.config.category),

        // 获取指定组下所有曲目接口
        musicOfGroup : _Url.getMusicsOfOneGroup,

        // 上传MP3接口
        uploadMp3 : _Url.uploadMp3,

        // 添加音乐到指定组接口
        addMusicsToSpecial : _Url.addMusicsToSpecial,
        
        // 删除指定组音乐接口
        removeMusicsFromSpecial : _Url.removeMusicsFromSpecial,

        table : $("#J_Data_Table"),
        topListWrapper : $("#J_Groups_Wrapper"),
        topList : $("#J_Groups_List"),
        topLeftArrow : $("#J_Left_Arrow"),
        topRightArrow : $("#J_Right_Arrow"),
        addMusicBtn : $("#J_Music_Add"),
        deleteMusicBtn : $("#J_Music_Delete"),
        upMusicBtn : $("#J_Music_Up"),
        downMusicBtn : $("#J_Music_Down"),
        assignMusicBtn : $("#J_Music_Assign"),

        // 页面打开默认选中组别
        topList_defalut : 0
	});


	root["TrackManagement"] = TrackManagement;

}).call(this, jQuery, MusicBase);

$(function() {
	window.trackManagement = new TrackManagement();
});