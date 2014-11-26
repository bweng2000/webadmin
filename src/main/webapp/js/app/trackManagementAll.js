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

            this.getTableData();

            this.bindEvents();

		},

        bindEvents : function() {
            var _this = this;
            

            this.addMusicBtn.bind('click', function() {
                _this.addMusic();
            });

            this.deleteMusicBtn.bind('click', function() {
                _this.deleteMusic();
            });

            this.assignMusicBtn.bind('click', function() {
                _this.assignMusic();
            });

        },

        initUploadWidget : function() {
            var _this = this,
                uploadtxt = "上传附件",
                divStatus = $("#divStatus"),
                datetrigger = $("#J_Playtime"),

                settings = {
                flash_url : "../../swfupload/swfupload.swf",
                upload_url: _this.uploadMp3,
                file_post_name : "file", // 是POST过去的$_FILES的数组名
                post_params: {"PHPSESSID" : ""},
                file_size_limit : 5*1024,
                file_types : "*.*",
                file_types_description : "All Files",
                file_upload_limit : 100,
                file_queue_limit : 1,
                custom_settings : {
                    progressTarget : "fsUploadProgress",
                    cancelButtonId : "btnCancel"
                },
                debug: false,

                // Button settings
                button_image_url: "../../images/TestImageNoText_65x29.png",
                button_width: "80",
                button_height: "30",
                button_placeholder_id: "spanButtonPlaceHolder",
                button_text: '<span class="theFont">' + uploadtxt + '</span>',
                button_text_style: ".theFont { font-family: Microsoft Yahei, Helvetica, Arial, sans-serif;font-size: 14px; }",
                button_text_left_padding: 10,
                button_text_top_padding: 3,
                button_cursor: SWFUpload.CURSOR.HAND,
                
                // The event handler functions are defined in handlers.js
                file_queued_handler : fileQueued,
                file_queue_error_handler : fileQueueError,
                file_dialog_complete_handler : fileDialogComplete,
                upload_start_handler : uploadStart,
                upload_progress_handler : uploadProgress,
                upload_error_handler : uploadError,
                upload_success_handler : uploadSuccess,
                upload_complete_handler : uploadComplete,
                queue_complete_handler : queueComplete  // Queue plugin event
            };

            divStatus.hide();
            new SWFUpload(settings);
            datetrigger.datetimepicker({
                showOn: "both",
                buttonImage: "../../images/calendar.png",
                buttonImageOnly: true,
                option:'zh',
                autoSize: true,
                defaultDate : '2014-11-10',
                changeMonth: true,
                changeYear: true, 
                timeFormat: 'hh:mm:ss',
                dateFormat: 'yy-mm-dd'
            });
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
                            
                            this.box.addClass("out-box2");
                            this.ibody.html(_html);
                            /*this.ibody.find('.J_Select_Wrapper1').append(list1.widget);
                            this.ibody.find('.J_Select_Wrapper2').append(list2.widget);
                            this.ibody.find('.J_Select_Wrapper3').append(list3.widget);

                            this.ibody.find('input[type="file"]').bind('change', function() {
                                
                                var element = $(this),
                                    fileTriggerE = element.prev();
                                    fileNameE = element.next();

                                fileTriggerE.html('重新上传');
                                fileNameE.html(this.value);
                                fileNameE.attr('title', this.value);

                            });*/


                            // 事件绑定
                            // 
                            _this.initUploadWidget();

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
                       /* _html = ['<form action="' + _this.uploadMp3 + '" target="J_Form_Target" method="POST" enctype="multipart/form-data">',
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
                                '</form>'].join('');*/

                        _html = ['<form action="' + _this.uploadMp3 + '" target="J_Form_Target" method="POST" enctype="multipart/form-data">',
                                    '<dl class="clearfix">',
                                        '<dt>上传曲目</dt>',
                                        '<dd>',
                                            '<div class="uploadbox">',
                                                '<div class="" id="uploadnew">',
                                                    '<div class="fieldset flash" id="fsUploadProgress">',
                                                        '<span id="spanButtonPlaceHolder"></span>',
                                                        '<input id="btnCancel" type="button" value="Cancel All Uploads" onclick="swfu.cancelQueue();" disabled="disabled" style="margin-left: 2px; font-size: 8pt; height: 29px; display:none"  />',
                                                        '<span class="legend" style="line-height:inherit;height:inherit;"></span>',
                                                    '</div>',
                                                    '<div id="divStatus" style="text-align:left;"></div>',
                                                '</div>',
                                                '<div class="" id="uploaded" style="text-align:left;">',
                                                    '<span id="attachfiletxt">还没有附件，请先上传</span>',
                                                '</div>',
                                                '<div id="attachfiletxt"><input name="attachfile" type="hidden" id="attachfile" value=""/></div>',
                                                '<input type="hidden" name="havelast" value="0" id="havelast" class="hide" />',
                                            '</div>',
                                        '</dd>',
                                        '<dt>播放时间</dt>',
                                        '<dd class="clearfix">',
                                            /*'<div class="j-select-wrapper J_Select_Wrapper1"></div>',
                                            '<div class="j-select-wrapper J_Select_Wrapper2"></div>',
                                            '<div class="j-select-wrapper no-mr J_Select_Wrapper3"></div>',*/
                                            '<input type="text" name="playtime" value="2014-11-10" id="J_Playtime" />',
                                        '</dd>',
                                    '</dl>',
                                '</form>'].join('');

                        $.isFunction(callback) && callback.call(it, _html/*, new DropList(LIST_DATA1), new DropList(LIST_DATA2), new DropList(LIST_DATA3)*/);
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
                url : _this.updateMusics,
                data : {
                    musics : "[" + toDeleteArray.join(',') + "]",
                },
                dataType : "json",
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
        getTableData : function() {

            this.tableSettings.ajax.url = this.getAllMusics;
            
            // 表格已经初始化
            if ($.fn.dataTable.isDataTable(this.table)) {
                this.table.fnDestroy();
            }

            this.table.dataTable(this.tableSettings);
            this._DataTable = this.table.DataTable();
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
                { "data" : "name" }/*,
                { "data" : "status" },
                { "data" : "special" },
                { "data" : "playtime" }*/
            ],
            "columnDefs" : [
                {
                    "targets": [0],
                    "orderable": false,
                    "defaultContent" : '<i class="j-checkbox"></i>'
                }
            ]
        },

        // 获取所有分组
        groupsUrl : _Url.getAllGroups,

        // 获取所有曲目接口
        getAllMusics : _Url.getAllMusics,
         
        // 更新曲目接口
        updateMusics : _Url.addAndRemoveMusics,

        // 上传MP3接口
        uploadMp3 : _Url.uploadMp3,


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