/**
 * 终端管理功能模块
 * 添加删除门店处理
 */
;(function($, M) {

    var root = this,
    document = root.document,

    TerminalManagement = M.inherit(M.BaseTable, {
        constructor : TerminalManagement,
        initialize : function() {

            var _this = this;

            this.wrap = this.wrap || $("body", document);

            this.table.dataTable(this.tableSettings);
            this._DataTable = this.table.DataTable();

            this.wrap.delegate('[action-type="add_stores"]', 'mousedown', function() {
                _this.addStores();
            });

            this.wrap.delegate('[action-type="delete_stores"]', 'mousedown', function() {
                _this.deleteStores();
            });

        },

        addStores : function() {

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

                            var storeElements = this.box.find('input[name="storeName"]');

                            // 事件绑定
                            this.box.delegate('.blue-button', 'mousedown', function() {
                                it.executeAdding({
                                    storeName : storeElements.val(),
                                    groupName : list.current
                                });
                            });

                            // 直接显示弹出层
                            this.show();
                        });
                    },
                    executeAdding : function(data) {
                        var it = this;

                        if (!data.storeName) {
                            alert("请输入门店名称");
                        } else {
                            $.ajax({
                                type: 'GET',
                                url: _Url.addStoresToGroup,
                                data: data,
                                dataType: "json",
                                success: function() {

                                    _this._DataTable.row.add({
                                        "deviceID" : 1,
                                        "storeName" : 2,
                                        "connectStatus" : false,
                                        "timeReported" : '2002'
                                    }).draw();
                                
                                    it.hide();
                                },
                                error : function(xhr, error, msg) {
                                    alert(msg);
                                }
                            });
                        }

                        
                    },
                    getDomTemplate : function(callback) {
                        var it = this;
                        this.getGroups(function(list) {
                            var _html = ['<dl class="clearfix">',
                                '<dt>门店名称</dt>',
                                '<dd><input type="text" name="storeName" value="" /></dd>',
                                '<dt>属于分组</dt>',
                                '<dd class="J_List_Wrapper"></dd>',
                            '</dl>'].join('');

                            $.isFunction(callback) && callback.call(it, _html, list);
                        });
                    },
                    getGroups : function(callback) {
                        var it = this;
                        $.ajax({
                            type : "GET",
                            url : _Url.getAllGroups,
                            dataType : "json",
                            success : function(data) {
                                $.isFunction(callback) && callback.call(it, new M.DropList(data));
                            },
                            error : function() {

                            }
                        });
                    }       
                };

                this.outBox = (new (M.inherit( M.PopupBox, _prototype, { title : '添加门店' } )));

            } else {
                this.outBox.show();
            }
        },
        deleteStores : function() {
            var _this = this,
            toDeleteTrs = this._DataTable.row('.selected');
            toDeleteTrNodes = toDeleteTrs.nodes();
            toDeleteArray = [],
            i = 0,
            l = toDeleteTrNodes.length;

        
            for (; i < l; i++) {
                var node = toDeleteTrNodes[i];
                toDeleteArray.push(node.getElementsByTagName('td')[2].innerHTML);
            }

            $.ajax({
                type : "PUT",
                url : _Url.removeStoresFormGroup,
                data : {
                    storeName : toDeleteArray,
                },
                dataType : "json",
                success : function(data) {
                    toDeleteTrs.remove().draw(false);
                },
                error : function() {

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
                "url" : _Url.androidStatus,
                "dataSrc" : ""
            },
            "columns" : [
                null,
                { "data" : "deviceID" },
                { "data" : "storeName" },
                { "data" : "connectStatus" },
                { "data" : "timeReported" }
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
        table : $("#J_Data_Table")
    });

    root["TerminalManagement"] = TerminalManagement;
    
}).call(this, jQuery, MusicBase);


$(function() {
    new TerminalManagement();
});