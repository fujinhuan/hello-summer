$(function () {

    var $colorBox = $('#sp-color');
    var $sizeBox = $('#sp-size');
    var $styleBox = $('#sp-style');
    var $colorSelect = $('#color-select');
    var $sizeSelect = $('#size-select');
    var $styleSelect = $('#style-select');
    var $thead = $('#sp-thead');
    var $tbody = $('#sp-tbody');

    var colors = [];
    var sizes = [];
    var styles = [];


    var _colors = [];
    var _sizes = [];
    var _styles = [];
    $.ajax({
        url: '/goods/get_spec_list_by_category',
        type: "post",
        dataType: "json",
        contentType: 'application/json',
        data: JSON.stringify({categoryId: 1}),
        success: function (res) {
            // console.log(res.data);
            if (res.code == 0 && res.data != null) {
                _colors = res.data.color;
                _sizes = res.data.size;
                _styles = res.data.style;
            }
        }
    });

    var ggData = window.ggData || [];

    // =====================================
    // =====================================
    // =====================================

    function getItemByColor(data, color) {
        var pcolor = color || '';
        var res = null;
        for (var i = 0, n = data.length; i < n; i++) {
            if (pcolor === data[i].color) {
                res = data[i];
                break;
            }
        }
        return res;
    }

    function getItemByColorSize(data, color, size) {
        var pcolor = color || '';
        var psize = size || '';
        var res = null;
        for (var i = 0, n = data.length; i < n; i++) {
            if (pcolor === data[i].color && psize === data[i].size) {
                res = data[i];
                break;
            }
        }
        return res;
    }

    function getItemByColorStyle(data, color, style) {
        var pcolor = color || '';
        var pstyle = style || '';
        var res = null;
        for (var i = 0, n = data.length; i < n; i++) {
            if (pcolor === data[i].color && pstyle === data[i].style) {
                res = data[i];
                break;
            }
        }
        return res;
    }

    function getItemByAll(data, color, size, style) {
        var pcolor = color || '';
        var psize = size || '';
        var pstyle = style || '';
        var res = null;
        for (var i = 0, n = data.length; i < n; i++) {
            if (pcolor === data[i].color && psize === data[i].size && pstyle === data[i].style) {
                res = data[i];
                break;
            }
        }
        return res;
    }

    function createColorItem() {
        var html = '<li>';
        html += '<input type="hidden" name="spec_color_id[]" value="0" /><input type="text" name="spec_color[]" autocomplete="off" />';
        html += '<span><img><input type="file" onchange="addImage(this)" /><input type="hidden" name="spec_image[]"/><em>+</em></span>';
        html += '<i>×</i>';
        html += '<div></div>';
        html += '</li>';
        $colorBox.append(html);
    }

    function createSizeItem() {
        var html = '<li>';
        html += '<input type="hidden" name="spec_size_id[]" value="0" /><input type="text" name="spec_size[]" autocomplete="off" >';
        html += '<i>×</i>';
        html += '<div></div>';
        html += '</li>';
        $sizeBox.append(html);
    }

    function createStyleItem() {
        var html = '<li>';
        html += '<input type="hidden" name="spec_style_id[]" value="0" /><input type="text" name="spec_style[]" autocomplete="off" >';
        html += '<i>×</i>';
        html += '<div></div>';
        html += '</li>';
        $styleBox.append(html);
    }

    function createHead(colors, sizes, styles) {
        if (colors.length < 1) return '';
        var html = '<tr><th><div>颜色</div></th>';
        html += (sizes.length > 0 ? '<th><div>尺码</div></th>' : '');
        html += (styles.length > 0 ? '<th><div>款式</div></th>' : '');
        html += '<th><div>编码</div></th><th><div>价格</div></th></tr>';
        return html;
    }

    function createBody(colors, sizes, styles) {
        var colorsLength = colors.length;
        var sizesLength = sizes.length;
        var stylesLength = styles.length;
        var html = '';
        // 默认三项 编码 价格 库存
        var createCPS = function (res) {
            var res = res || {};
            var _code = res.code || '';
            var _price = res.price || '';
            var _stock = res.stock || '';
            var tpl = '';
            tpl += '<td><input type="text" name="spec_code[]" placeholder="请输入编码" value="' + _code + '" /></td>';
            tpl += '<td><input type="text" name="spec_price[]" placeholder="请输入价格" value="' + _price + '" /></td>';
            // tpl += '<td><input type="text" name="spec_stock[]" placeholder="请输入库存" value="' + _stock + '" /></td>';
            return tpl;
        };
        // 颜色 -> 尺码
        var createBySize = function (color) {
            var tpl = '';
            for (var j = 0; j < sizesLength; j++) {
                tpl += '<tr>';
                tpl += '<td><div>' + sizes[j] + '</div></td>';
                tpl += createCPS(getItemByColorSize(ggData, color, sizes[j]));
                tpl += '</tr>';
            }
            return tpl;
        };
        // 颜色 -> 款式
        var createByStyle = function (color) {
            var tpl = '';
            for (var j = 0; j < stylesLength; j++) {
                tpl += '<tr>';
                tpl += '<td><div>' + styles[j] + '</div></td>';
                tpl += createCPS(getItemByColorStyle(ggData, color, styles[j]));
                tpl += '</tr>';
            }
            return tpl;
        };
        // 颜色 -> 尺码 -> 款式
        var createStyle = function (color, size) {
            var tpl = '';
            for (var k = 0; k < stylesLength; k++) {
                tpl += '<tr>';
                tpl += '<td><div>' + styles[k] + '</div></td>';
                tpl += createCPS(getItemByAll(ggData, color, size, styles[k]));
                tpl += '</tr>';
            }
            return tpl;
        };
        var createSize = function (color) {
            var tpl = '';
            for (var j = 0; j < sizesLength; j++) {
                tpl += '<tr>';
                tpl += '<td><div>' + sizes[j] + '</div></td>';
                tpl += '<td class="child" colspan="4"><table>';
                tpl += createStyle(color, sizes[j]);
                tpl += '</table></td></tr>';
            }
            return tpl;
        };

        if (colorsLength < 1) return html;

        // 颜色
        if (sizesLength < 1 && stylesLength < 1) {
            for (var i = 0; i < colorsLength; i++) {
                html += '<tr>';
                html += '<td><div>' + colors[i] + '</div></td>';
                html += createCPS(getItemByColor(ggData, colors[i]));
                html += '</tr>';
            }
        }

        // 颜色 -> 尺码
        if (sizesLength > 0 && stylesLength < 1) {
            for (var i = 0; i < colorsLength; i++) {
                html += '<tr>';
                html += '<td><div>' + colors[i] + '</div></td>';
                html += '<td class="child" colspan="4"><table>';
                html += createBySize(colors[i]);
                html += '</table></td></tr>';
            }
        }

        // 颜色 -> 款式
        if (sizesLength < 1 && stylesLength > 0) {
            for (var i = 0; i < colorsLength; i++) {
                html += '<tr>';
                html += '<td><div>' + colors[i] + '</div></td>';
                html += '<td class="child" colspan="4"><table>';
                html += createByStyle(colors[i]);
                html += '</table></td></tr>';
            }
        }

        // 颜色 尺码 款式
        if (sizesLength > 0 && stylesLength > 0) {
            for (var i = 0; i < colorsLength; i++) {
                html += '<tr>';
                html += '<td><div>' + colors[i] + '</div></td>';
                html += '<td class="child" colspan="5"><table>';
                html += createSize(colors[i]);
                html += '</table></td></tr>';
            }
        }

        return html;
    }

    function render() {

        colors = [];
        sizes = [];
        styles = [];

        $colorBox.find('li').each(function (i, li) {
            var color = $(li).find('input[type="text"]').val();
            if (color != '') {
                colors.push(color);
            }
        });
        $sizeBox.find('li').each(function (i, li) {
            var size = $(li).find('input[type="text"]').val();
            if (size != '') {
                sizes.push(size);
            }
        });
        $styleBox.find('li').each(function (i, li) {
            var style = $(li).find('input[type="text"]').val();
            if (style != '') {
                styles.push(style);
            }
        });

        $thead.html(createHead(colors, sizes, styles));
        $tbody.html(createBody(colors, sizes, styles));
    }


    // 页面加载时 ajax获取数据 
    // 根据数据 生成规格数据表
    // $.ajax({
    // 	success: function(res) {
    // 		ggData = res.data;
    // 		render();
    // 	}
    // });
    var goodsId = $("#goodsId").val();
    if (goodsId != undefined && goodsId > 0) {
        console.log("开始加载规格数据：" + goodsId);
        $.ajax({
            url: '/api/getGoodsSpecList',
            type: "POST",
            dataType: 'json',
            contentType: 'application/json',
            data: JSON.stringify(goodsId),
            success: function (res) {
                console.log(res.data);
                if (res.code == 0) {
                    for (var i = 0; i < res.data.length; i++) {
                        ggData.push({
                            color: '' + res.data[i].colorValue + '',
                            size: '' + res.data[i].sizeValue + '',
                            style: '' + res.data[i].styleValue + '',
                            code: '' + res.data[i].specNumber + '',
                            price: '' + res.data[i].price + '',
                            stock: '500'
                        })
                    }
                    render();//加载规格数据
                }

            }
        });//ajax获取规格
    }
    // render(); // 使用ajax加载数据时删除这一行 和 页面中的模拟数据

    // =====================================
    // =====================================
    // =====================================


    // 添加颜色规格
    $colorSelect.click(function (e) {
        e.stopPropagation();
        createColorItem();
    });
    // 删除颜色规格
    $colorBox.delegate('i', 'click', function (e) {
        e.stopPropagation();
        $(this).parent('li').remove();
        render();
    });
    // 输入框获取焦点弹出 颜色选择框
    $colorBox.delegate('input[type="text"]', 'click', function (e) {
        e.stopPropagation();
        var $div = $(this).next('span').next('i').next('div');
        // $.ajax.. 获取颜色数据
        // ..
        // var _colors = ['黑色', '白色', '红色', '绿色', '蓝色', '蓝青色', '中灰色', '紫色'];
        var htmlTpl = (function () {
            for (var i = 0, n = _colors.length, html = ''; i < n; i++) {
                html += '<p data-id="' + _colors[i].id + '">' + _colors[i].value + '</p>';
            }
            return html;
        }());
        $div.html(htmlTpl).show();
    });
    // 输入框失去焦点 
    $colorBox.delegate('input[type="text"]', 'blur', function (e) {
        var $input = $(this);
        var $div = $input.next('span').next('i').next('div');
        var $color = $input.val();
        // $.ajax.. 专递后台 $color
        // ..

        render();
    });
    //  颜色选择框 点击选择颜色
    $colorBox.delegate('p', 'click', function (e) {
        e.stopPropagation();
        var $this = $(this);
        var $div = $this.parent('div');
        var $input = $div.prev('i').prev('span').prev('input');
        $input.val($this.text());
        $div.prev('i').prev('span').prev('input').prev('input').val($this.attr("data-id"));
        $div.html('').hide();
        render();
    });
    $(document).click(function () {
        $colorBox.find('li div').hide();
    });

    // =====================================
    // =====================================
    // =====================================


    // 添加尺码规格
    $sizeSelect.click(function (e) {
        e.stopPropagation();
        createSizeItem();
    });
    // 删除尺码规格
    $sizeBox.delegate('i', 'click', function (e) {
        e.stopPropagation();
        $(this).parent('li').remove();
        render();
    });
    // 输入框获取焦点弹出 尺码选择框
    $sizeBox.delegate('input[type="text"]', 'click', function (e) {
        e.stopPropagation();
        var $div = $(this).next('i').next('div');
        // $.ajax.. 获取尺码数据
        // ..
        // var _sizes = ['S', 'M', 'L', 'XL', 'XXL', 'XXXL'];
        var htmlTpl = (function () {
            for (var i = 0, n = _sizes.length, html = ''; i < n; i++) {
                html += '<p data-id="' + _sizes[i].id + '">' + _sizes[i].value + '</p>';
            }
            return html;
        }());
        $div.html(htmlTpl).show();
    });
    // 输入框失去焦点 
    $sizeBox.delegate('input[type="text"]', 'blur', function (e) {
        var $input = $(this);
        var $div = $input.next('i').next('div');
        var $size = $input.val();
        // $.ajax.. 专递后台 $color
        // ..

        render();
    });
    //  尺码选择框 点击选择尺码
    $sizeBox.delegate('p', 'click', function (e) {
        e.stopPropagation();
        var $this = $(this);
        var $div = $this.parent('div');
        var $input = $div.prev('i').prev('input');
        $input.val($this.text());
        $div.prev('i').prev('input').prev('input').val($this.attr("data-id"));
        $div.html('').hide();
        render();
    });
    $(document).click(function () {
        $sizeBox.find('li div').hide();
    });

    // =====================================
    // =====================================
    // =====================================


    // 添加款式规格
    $styleSelect.click(function (e) {
        e.stopPropagation();
        createStyleItem();
    });
    // 删除款式规格
    $styleBox.delegate('i', 'click', function (e) {
        e.stopPropagation();
        $(this).parent('li').remove();
        render();
    });
    // 输入框获取焦点弹出 款式选择框
    $styleBox.delegate('input[type="text"]', 'click', function (e) {
        e.stopPropagation();
        var $div = $(this).next('i').next('div');
        // $.ajax.. 获取款式数据
        // ..
        // var _styles = ['胖胖的', '瘦瘦的', '高高的', '漂亮的', '性感的', '修身的'];
        var htmlTpl = (function () {
            for (var i = 0, n = _styles.length, html = ''; i < n; i++) {
                html += '<p data-id="' + _styles[i].id + '">' + _styles[i].value + '</p>';
            }
            return html;
        }());
        $div.html(htmlTpl).show();
    });
    // 输入框失去焦点 
    $styleBox.delegate('input[type="text"]', 'blur', function (e) {
        var $input = $(this);
        var $div = $input.next('i').next('div');
        var $style = $input.val();
        // $.ajax.. 专递后台 $color
        // ..

        render();
    });
    //  款式选择框 点击选择尺码
    $styleBox.delegate('p', 'click', function (e) {
        e.stopPropagation();
        var $this = $(this);
        var $div = $this.parent('div');
        var $input = $div.prev('i').prev('input');
        $input.val($this.text());
        $div.prev('i').prev('input').prev('input').val($this.attr("data-id"));
        $div.html('').hide();
        render();
    });
    $(document).click(function () {
        $styleBox.find('li div').hide();
    });
});