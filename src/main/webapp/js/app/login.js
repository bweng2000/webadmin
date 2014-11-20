/**
 * 登陆界面功能模块
 * 用户密码登陆
 * 登陆错误信息提示
 */

;(function() {

	var root = this;

	UAMusicLogin = {
		usernameid : 'J_Username',
		passwordid : 'J_Password',
		stautsid : 'J_Status',
		submitid : 'J_Submit',
		redirect : '../pages/admin/terminal_management.html',

		init : function() {
			var _this = this;

			this.username = $("#" + this.usernameid);
			this.password = $("#" + this.passwordid);
			this.statusQuote = $("#" + this.stautsid);
			this.statusInfo = this.statusQuote.find('p');
			this.submit = $("#" + this.submitid);

			this.statusQuote.hide();

			this.submit.bind("mousedown", function(e) {
				var usernameVal = $.trim(_this.username.val());
				var passwordVal = $.trim(_this.password.val());
				_this.login(usernameVal, passwordVal);
			});
			
			$(document).bind("keydown", function(e) {
				if (e.keyCode === 13) {
					var usernameVal = $.trim(_this.username.val());
					var passwordVal = $.trim(_this.password.val());
					_this.login(usernameVal, passwordVal);
				}
			});
		},

		login : function(uname, pword) {
			var _this = this;

			if (!uname) {
				this.showError("请输入用户名");
			} else if (!pword) {
				this.showError("请输入用户密码");
			} else {
				$.ajax({
					url : root._Url.login,
					data : {
						username : uname,
						password : pword
					},
					contentType : 'application/x-www-form-urlencoded; charset=utf-8',
					type : "POST",
					//dataType : "json",
					success : function() {	
						root.location.href = _this.redirect;
					},
					error : function(jqXHR, status, error) {
						_this.showError(status);
					}
				});
			}
		},

		showError : function(msg) {
			if (!!$.trim(msg)) {
				this.statusInfo.html(msg);
				this.statusQuote.fadeIn("fast");
			}	
		},

		hideError : function(isclear) {
			isclear && this.statusInfo.html('');
			this.statusQuote.fadeOut("fast");
		}
	};


	UAMusicLogin.init();

}).call(this);