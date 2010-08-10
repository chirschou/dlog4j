function copy_to_clipboard(meintext) {
	if (window.clipboardData) {   
		// the IE-manier
		window.clipboardData.setData("Text", meintext);   
	}
	else if (window.netscape){    
		// dit is belangrijk maar staat nergens duidelijk vermeld:
		netscape.security.PrivilegeManager.enablePrivilege('UniversalXPConnect');   
		// maak een interface naar het clipboard
		var clip = Components.classes['@mozilla.org/widget/clipboard;1'].createInstance(Components.interfaces.nsIClipboard);
		if (!clip) return;   
		// maak een transferable
		var trans = Components.classes['@mozilla.org/widget/transferable;1'].createInstance(Components.interfaces.nsITransferable);
		if (!trans) return;   
		// specificeer wat voor soort data we op willen halen; text in dit geval
		trans.addDataFlavor('text/unicode');   
		// om de data uit de transferable te halen hebben we 2 nieuwe objecten nodig   om het in op te slaan
		var str = new Object();
		var len = new Object();   
		var str = Components.classes["@mozilla.org/supports-string;1"].createInstance(Components.interfaces.nsISupportsString);   
		var copytext=meintext;   
		str.data=copytext;   
		trans.setTransferData("text/unicode",str,copytext.length*2);   
		var clipid=Components.interfaces.nsIClipboard;
		if (!clip) return false;
		clip.setData(trans,null,clipid.kGlobalClipboard);   
   }
   return false;
}

function get_from_clipboard() {
	if (window.clipboardData) {
		// the IE-manier
		return(window.clipboardData.getData('Text'));
	} 
	else if (window.netscape) { 
		netscape.security.PrivilegeManager.enablePrivilege('UniversalXPConnect');
		var clip = Components.classes['@mozilla.org/widget/clipboard;1'].createInstance(Components.interfaces.nsIClipboard);
		if (!clip) return;

		var trans = Components.classes['@mozilla.org/widget/transferable;1'].createInstance(Components.interfaces.nsITransferable);
		if (!trans) return;
    
		trans.addDataFlavor('text/unicode');
		clip.getData(trans,clip.kGlobalClipboard);

		var str = new Object();
		var len = new Object();
    
		try { trans.getTransferData('text/unicode',str,len); }
		catch(error) { return; }

		if (str) {
			if (Components.interfaces.nsISupportsWString) str=str.value.QueryInterface(Components.interfaces.nsISupportsWString);     
			else if (Components.interfaces.nsISupportsString) str=str.value.QueryInterface(Components.interfaces.nsISupportsString);
			else str = null;
		}

		if (str) return(str.data.substring(0,len.value / 2));
	}
	return;
}

function select_all(n,v){
    var s=document.getElementsByTagName('input');
    for (var i=0,c;c=s[i];i++){
        if (c.name==n)
            c.checked=v;
    }
    return false;
}

function hide_div(entryid){
    document.getElementById(entryid).style.display = "none";
}

function show_div(entryid){
    document.getElementById(entryid).style.display = "block";
}

function ltrim(str){
    var whitespace = new String(" \t\n\r");
    var s = new String(str);
    if (whitespace.indexOf(s.charAt(0)) != -1){
        var j=0, i = s.length;
        while (j < i && whitespace.indexOf(s.charAt(j)) != -1){
            j++;
        }
        s = s.substring(j, i);
    }
    return s;
}


function rtrim(str){
    var whitespace = new String(" \t\n\r");
    var s = new String(str);
    if (whitespace.indexOf(s.charAt(s.length-1)) != -1){
        var i = s.length - 1;
        while (i >= 0 && whitespace.indexOf(s.charAt(i)) != -1){
            i--;
        }
        s = s.substring(0, i+1);
    }
    return s;
}

 
function trim(str){
    return rtrim(ltrim(str));
}


function xml_encode(str){
       str=trim(str);
       str=str.replace("&","&amp;");
       str=str.replace("<","&lt;");
       str=str.replace(">","&gt;");
       str=str.replace("'","&apos;");
       str=str.replace("\"","&quot;");
       return str;
}


function is_empty(str){
    return trim(str)=="";
}

function not_empty(str){
    return trim(str)!="";
}

function equals(str1, str2){
	return str1 == str2;
}

 
function is_int(objStr,sign,zero){
    var reg;    
    var bolzero;    
    if(trim(objStr)==""){
        return false;
    }
    else{
        objStr=objStr.toString();
    }
    
    if((sign==null)||(trim(sign)=="")){
        sign="+-";
    }
    if((zero==null)||(trim(zero)==""))
    {
        bolzero=false;
    }
    else{
        zero=zero.toString();
        if(zero=="0"){
            bolzero=true;
        }
    }

    switch(sign)

    {
        case "+-":
            reg=/(^-?|^\+?)\d+$/;            
            break;
        case "+": 
            if(!bolzero){
                reg=/^\+?[0-9]*[1-9][0-9]*$/;
            }
            else{
                reg=/^\+?[0-9]*[0-9][0-9]*$/;
            }
            break;
        case "-":
            if(!bolzero){
            	reg=/^-[0-9]*[1-9][0-9]*$/;
            }
            else{
                reg=/^-[0-9]*[0-9][0-9]*$/;
            }            
            break;
        default:
            return false;
            break;
    }

    var r=objStr.match(reg);
    if(r==null){
        return false;
    }
    else{
        return true;     
    }
}

 

function is_float(objStr,sign,zero){
    var reg;    
    var bolzero;    
    if(trim(objStr)==""){
        return false;
    }
    else{
        objStr=objStr.toString();
    }    

    if((sign==null)||(trim(sign)=="")){
        sign="+-";
    }
    if((zero==null)||(Trim(zero)=="")){
        bolzero=false;
    }
    else{
        zero=zero.toString();
        if(zero=="0"){
            bolzero=true;
        }
    }
    switch(sign){
        case "+-":
            reg=/^((-?|\+?)\d+)(\.\d+)?$/;
            break;
        case "+": 
            if(!bolzero){
                reg=/^\+?(([0-9]+\.[0-9]*[1-9][0-9]*)|([0-9]*[1-9][0-9]*\.[0-9]+)|([0-9]*[1-9][0-9]*))$/;
            }
            else{
                reg=/^\+?\d+(\.\d+)?$/;
            }
            break;
        case "-":
            if(!bolzero){
                reg=/^-(([0-9]+\.[0-9]*[1-9][0-9]*)|([0-9]*[1-9][0-9]*\.[0-9]+)|([0-9]*[1-9][0-9]*))$/;
            }
            else{
                reg=/^((-\d+(\.\d+)?)|(0+(\.0+)?))$/;
            }            
            break;
        default:
            return false;
            break;
    }

    var r=objStr.match(reg);
    if(r==null){
        return false;
    }
    else{        
        return true;     
    }
}

function is_email(email)
{
	// a very simple email validation checking. 
	// you can add more complex email checking if it helps 
    var splitted = email.match("^(.+)@(.+)$");
    if(splitted == null) return false;
    if(splitted[1] != null )
    {
      var regexp_user=/^\"?[\w-_\.]*\"?$/;
      if(splitted[1].match(regexp_user) == null) 
      	return false;
    }
    if(splitted[2] != null)
    {
      var regexp_domain=/^[\w-\.]*\.[A-Za-z]{2,4}$/;
      if(splitted[2].match(regexp_domain) == null) 
      {
	    var regexp_ip =/^\[\d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3}\]$/;
	    if(splitted[2].match(regexp_ip) == null) 
	    	return false;
      }// if
      return true;
    }
	return false;
}