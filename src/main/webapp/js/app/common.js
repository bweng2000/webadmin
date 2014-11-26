;(function($) {

	var root = this,
	document = root.document,
	_MusicBase = root.MusicBase,
	_publicProto = {},
	_PopUpBox,
	_DropList,
	_BaseTable,
	_LogOut,

	// 基本对象
	MusicBase = function() {

	};

	MusicBase.prototype = {
		constructor : MusicBase,
		version : '0.0.0',
		author : ''
	};

	// 直接对MusicBase静态扩充
	_publicProto = {
		// 返回一个最多执行一次的函数
		once : function(func) {
		    var ran = false, memo;
		    return function() {
		        if (ran) return memo;
		        ran = true;
		        memo = func.apply(this, arguments);
		        func = null;
		        return memo;
		    };
		},
		// 继承封装
		inherit : (function() {
	        var extend = $.extend,
	        createObject = Object.create ?
	        function( proto, c ) {
	            return Object.create( proto, {
	                constructor: {
	                    value: c
	                }
	            } );
	        } :
	        function ( proto, c ) {
	            function F() {
	            }

	            F.prototype = proto;

	            var o = new F();
	            o.constructor = c;
	            return o;
	        },
	       
	        inherit = function( derived, base, px ) {
	            if ( !base || !derived ) {
	                return derived;
	            }
	            var baseProto = base.prototype, derivedProto;
	            derivedProto = createObject( baseProto, derived );
	            derived.prototype = extend( derivedProto, derived.prototype );
	            derived.superclass = createObject( baseProto, base );

	            if ( px ) {
	               extend( derivedProto, px );
	            }
	            
	            return derived;
	        },

	        deepInherit = function( base, px, sx ) {
	            var F = function() {
	                if ( sx ) {
	                    extend( this, sx );
	                }
	                base.apply( this, arguments );
	            };
	            inherit( F, base, px );
	            return F;
	        };

	        return deepInherit;
	    })(),
	    distinct : function( arr ) {
            var _a = arr.concat().sort();
            _a.sort( function( a,b ) {
                if( a == b ) {
                    var n = arr.indexOf( a );
                    arr.splice( n,1 );
                }
            } );
            return arr;
        },
	    getUniqueKey : function() {
	    	return Math.floor(Math.random() * 1000) + new Date().getTime().toString();
	    },
	    scrollTo : function(element, callback) {
			var offsetTop;
			if ($.isNumeric(element)) {
				offsetTop = element;
			} else {
				offsetTop = element.offset().top
			}
			$("body, html").animate({scrollTop : offsetTop}, 500, MusicBase.once(function() {
				$.isFunction(callback) && callback.apply(this, arguments);
			}));
		},
		center : function(obj, callback) {
			var left = $(document).scrollLeft() + ($(root).width() - obj.outerWidth()) / 2;
			var top = $(document).scrollTop() + ($(root).height() - obj.outerHeight()) / 2;

			obj.css("left", left);
			obj.css("top", top);
			$.isFunction(callback) && callback.apply(this, arguments);
		},
	    noConflict : function() {
	    	if (root.MusicBase === MusicBase) {
	    		root.MusicBase = _MusicBase;
	    	}

	    	return MusicBase;
	    }
	};

	$.extend(MusicBase, _publicProto);



	/**
	 * 弹出框组件基类
	 * 包含基本弹出关闭事件
	 * 基本定位事件和相应回调
	 */
	 _PopupBox = function(options) {
		var PREFIX = 'Musicbox_',

		_maskId = PREFIX + 'mask_' + MusicBase.getUniqueKey(),
		_boxId = PREFIX + 'box_' + MusicBase.getUniqueKey();

		this.getMaskId = function() {
			return _maskId;
		};
		this.getBoxId = function() {
			return _boxId;
		};
		this.wrap = (options && options.wrap) || $("body");
		this.displayStatus = 'close';

		$.extend(this, options);
		
		this.init.apply(this, arguments);
	},

	_PopupBox.prototype = {
		constructor : _PopupBox,
		init : function() {
			var _this = this;
			this.mask = $('<div class="out-box-mask" id="' + this.getMaskId() + '" style="display:none"></div>').prependTo(this.wrap);
			this.box = $([	'<div class="out-box" id="' + this.getBoxId() + '" style="display:none">',
								'<div class="out-box-title">',
									'<div class="line"></div>',
	            					'<h3>' + (this.title || '') + '</h3>',
	        					'</div>',
	        					'<div class="out-box-body"></div>', 
								'<div class="out-box-foot">',
	            					'<a href="javascript:;" class="blue-button">保存</a>',
	            					'<a href="javascript:;" class="grey-button">取消</a>',
	        					'</div>',
	        				'</div>']
        				.join("")).prependTo(this.wrap);

			this.box.delegate('.grey-button', 'mousedown', function() {
				_this.hide();
			});

			$.isFunction(this.initialize) && this.initialize.apply(this, arguments);
		},
		showMask : function(callback) {
			if (this.mask && this.mask.size() > 0) {
				this.mask.css("height", Math.max($("html").height(), $("body").height()) + 'px');
				this.mask.fadeIn("fast", function(){
					$.isFunction(callback) && callback.apply(this, arguments);
				});
			}
		},
		hideMask : function(callback) {
			if (this.mask && this.mask.size() > 0) {		
				this.mask.fadeOut("fast", function(){
					$.isFunction(callback) && callback.apply(this, arguments);
				});
			}
		},
		show : function() {
			var _this = this;
			this.displayStatus = 'open';
			MusicBase.center(this.box, function() {
				_this.showMask();
				_this.box.fadeIn('fast');
			});
		},
		hide : function() {
			var _this = this;
			this.displayStatus = 'close';
			this.mask.fadeOut('fast');
			this.box.fadeOut('fast');
		},
		destory : function() {
			this.displayStatus = 'close';
			this.mask.remove();
			this.box.remove();
		}
	};

	/**
	 * 模拟下拉列表组件
	 */
	_DropList = function(data) {
		this.data = data;
		this.init.apply(this, arguments);
	};

	_DropList.prototype = {
		constructor : _DropList,
		DOMS_TEMPLATE : '<div class="j-select">' + 
							'<div class="j-select-title">' + 
								'<span>{current}</span><i></i>' + 
							'</div>' + 
							'<div class="j-select-list"><ul style="display:none">{list}</ul></div>' + 
						'</div>',
		init : function() {
			var _this = this;

			this.getListDom(this.data);
			this.widget = $(this.DOMS_TEMPLATE.replace(/\{current\}/gi, this.current).replace(/\{list\}/gi, this.list));
			this.floatListWrapper = this.widget.find(".j-select-list");
			this.floatList = this.widget.find('ul');
			this.currentElement = this.widget.find('.j-select-title span');

			this.widget.delegate('.j-select-title', 'mousedown', function() {
				_this.floatList.toggle();
			});

			this.widget.delegate('ul>li', "mousedown", function() {
				_this.select($(this));
			});
		},
		select : function(element) {
			var val = $.trim(element.html());
			this.currentLi && this.currentLi.removeClass('selected');
			element.addClass('selected');
			this.currentLi = element;
			this.current = val;
			this.currentElement.html(val);
			this.floatList.hide();
		},
		getListDom : function(data) {
            var i = 0, 
            l = data.length,
            _str = [];

            for (; i < l; i++) {
                var item = data[i];
        		i === 0 && (this.current = item["groupName"]);
                _str.push('<li>' + item["groupName"] + '</li>');
            }

            this.list = _str.join('');
        }
	};


	/**
	 * 表格默认行为
	 * 处理复选框事件，由此基类派生出的表格均自动拥有此功能
	 */
	_BaseTable = function() {
		this.init.apply(this, arguments);
	};

	_BaseTable.prototype = {
		constructor : _BaseTable,
		init : function() {
			var _this = this;
			
			if (!this.table) return;

			this.table.delegate('tbody tr', 'click', function() {
				$(this).toggleClass('selected');
			});
		

            this.table.delegate('thead .j-checkbox', 'click', function() {
                var elements = $(this).parents('tr');
                alltrs = _this.table.find('tr');
                if (elements.hasClass('selected')) {
                    alltrs.removeClass('selected');
                } else {
                    alltrs.addClass('selected');
                }
            });

			$.isFunction(this.initialize) && this.initialize.apply(this, arguments);
		}
	};


	/**
	 * 登出功能
	 */
	_LogOut = {
		trigger : '[func="logout"]',
		redirect : '/webadmin/pages/login.html',
		init : function() {
			var _this = this;
			$("body", document).delegate(this.trigger, 'click', function() {
				$.ajax({
					url : root._Url.logout,
					data : null,
					//contentType : 'application/x-www-form-urlencoded; charset=utf-8',
					type : "GET",
					//dataType : "json",
					success : function() {	
						root.location.href = _this.redirect;
					},
					error : function(jqXHR, status, error) {
						alert(status);
					}
				});
			});
		}
	};

	_LogOut.init();

	MusicBase.PopupBox = _PopupBox;
	MusicBase.DropList = _DropList;
	MusicBase.BaseTable = _BaseTable;
	root["MusicBase"] = MusicBase;



}).call(this, jQuery);


// Test
/*
(new (MusicBase.inherit(MusicBase.PopupBox, null, {title:'添加分组'})))
.show();

new MusicBase.DropList([{
    "id": 1000,
    "groupName": "长宁区",
    "category": "区域",
    "expireDate": null
}]);
*/