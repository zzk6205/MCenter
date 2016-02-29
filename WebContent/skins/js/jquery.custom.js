(function($) {
	var showAlertTimer = 0;
	$.custom = {
			alert : function(message) {
			if (!message) {
				return;
			}
			if (showAlertTimer) {
				clearTimeout(showAlertTimer);
			}
			if ($("#jQueryAlertView")) {
				$("#jQueryAlertView").remove();
			}
			var d = $('<div id="jQueryAlertView"><label id="jQueryAlertViewText"></label></div>');
			d.css("position", "absolute")
				.css("display", "none")
				.css("z-index", "3000")
				.css("min-width", "200px")
				.css("max-width", "320px")
				.css("min-height", "30px")
				.css("max-height", "100px")
				.css("line-height", "28px")
				.css("overflow", "hidden")
				.css("padding", "0 10px")
				.css("text-align", "center")
				.css("background", "#000000")
				.css("color", "#FFFFFF")
				.css("opacity", ".7")
				.css("filter", "Alpha(Opacity = 70)")
				.css("border-radius", "4px")
				.css("font-family", "宋体");
			$("body").append(d);
			var alertView = $("#jQueryAlertView");
			var label = $("#jQueryAlertViewText");
			label.html(message);
			alertView.stop();
			var y = $(window).height() * 0.85;
			var x = ($(window).width() - alertView.width() - 20) / 2;
			alertView.css({ top : y, left : x }).hide();
			alertView.animate({ top : y - 50, opacity : 'show' }, 500);
			showAlertTimer = setTimeout(function() {
				alertView.animate({ top : y - 100, opacity : 'hide' }, 500);
				if ($("#jQueryAlertView")) {
					$("#jQueryAlertView").remove();
				}
			}, 1000);
		}
	};
})(jQuery);