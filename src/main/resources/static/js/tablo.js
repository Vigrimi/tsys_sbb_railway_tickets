jQuery(function(){
jQuery('input').keyup(function()
{ jQuery(this).next().find('.line_text').html(this.value)}).keyup();
})