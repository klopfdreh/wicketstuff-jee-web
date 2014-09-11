/**
 * Adds a value as get argument to the url
 * 
 * 
 * Example: 
 * 
 * var url = '${wicket:ajaxCallbackUrl()}';
 * Wicket.Ajax.ApplyParameter(url,{param:value});
 */
Wicket.Ajax.ApplyParameters = function(url,options){
	return url+"&"+$.param(options);
}

/**
 * Wraps the url into a Wicket.Ajax.Get-Call
 * 
 * Example:
 * 
 * var url = '${wicket:ajaxCallbackUrl()}';
 * Wicket.Ajax.WrapGet(url);
 * 
 */
Wicket.Ajax.WrapGet = function(url){
	 eval("Wicket.Ajax.get({'u':'"+url+"'});");
}
