var _paq = _paq || [];

_paq.push([function() {
	var self = this;
	function getOriginalVisitorCookieTimeout() {
		var now = new Date(),
		nowTs = Math.round(now.getTime() / 1000),
		visitorInfo = self.getVisitorInfo();
		var createTs = parseInt(visitorInfo[2]);
		var cookieTimeout = 33696000; // 13 mois en secondes
		var originalTimeout = createTs + cookieTimeout - nowTs;
		return originalTimeout;
	}
	this.setVisitorCookieTimeout( getOriginalVisitorCookieTimeout() );
}]);

_paq.push(['enableLinkTracking']);

fr_univlorraine_tools_vaadin_PiwikAnalyticsTracker = function() {

	this.setPiwikAccountCommand = function(trackerurl, siteid) {
		var u="//"+trackerurl+"/";
		_paq.push(['setTrackerUrl', u+'piwik.php']);
		_paq.push(['setSiteId', siteid]);
		var d=document, g=d.createElement('script'), s=d.getElementsByTagName('script')[0];
		g.type='text/javascript'; g.async=true; g.defer=true; g.src=u+'piwik.js'; s.parentNode.insertBefore(g,s);
	}

	this.pushCommand = function(command) {
		_paq.push(command);
	}

};
