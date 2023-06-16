function goodsSelect(goodsUrl, itemsUrl) {

    var $listBox = null;
    var $pageBox = null;
    var $query = null;
    var $button = null;
    var $selectAll = null;

    var spItems = []; // 参加的活动的商品
    var pageItems = []; // 当前分页中的商品
    var itemQuery = ''; // 查询关键字
    var pageNums = 8; // 每页显示多少条
    var pageIndex = 1; // 当前第几页
    var pageTotal = 1; // 初始设置共有多少页

    var checkItem = function (id) {
        for (var i = 0, n = spItems.length, res = false; i < n; i++) {
            if (id == spItems[i].id) {
                res = true;
                break;
            }
        }
        return res;
    };
    var addItem = function (id) {
        if (!checkItem(id)) {
            spItems.push(findItem(id));
        }
    };
    var removeItem = function (id) {
        for (var i = 0, n = spItems.length; i < n; i++) {
            if (id == spItems[i].id) {
                spItems.splice(i, 1);
                break;
            }
        }
    };
    var findItem = function (id) {
        for (var i = 0, n = pageItems.length, item = null; i < n; i++) {
            if (id == pageItems[i].id) {
                item = pageItems[i];
                break;
            }
        }
        return item;
    };

    var createList = function () {
        for (var i = 0, n = pageItems.length, html = ''; i < n; i++) {
            html += '<li>';
            if (checkItem(pageItems[i].id)) {
                html += '<input type="checkbox" checked="checked" value="' + pageItems[i].id + '" />';
            } else {
                html += '<input type="checkbox" value="' + pageItems[i].id + '" />';
            }
            html += '<img src="' + pageItems[i].image + '" />';
            html += '<span>' + pageItems[i].title + '</span>';
            html += '<b>' + pageItems[i].goodsNumber + '</b>';
            html += '</li>';
        }
        return html;
    };
    var createPage = function () {
        var html = '';
        if (pageTotal < 2) return html;
        if (pageIndex != 1) {
            html += '<span class="prev">&lt;</span>';
        }
        if (pageTotal < 11) {
            for (var i = 1; i <= pageTotal; i++) {
                if (pageIndex == i) {
                    html += '<span class="active">' + i + '</span>';
                    continue;
                }
                html += '<span>' + i + '</span>';
            }
        }
        if (pageTotal > 10) {
            if (pageIndex < 8) {
                for (var i = 1; i <= 8; i++) {
                    if (pageIndex == i) {
                        html += '<span class="active">' + i + '</span>';
                        continue;
                    }
                    html += '<span>' + i + '</span>';
                }
                html += '<span class="none">...</span>';
                html += '<span>' + pageTotal + '</span>';
            } else if (pageIndex >= 8 && pageIndex <= pageTotal - 7) {
                html += '<span>1</span>';
                html += '<span class="none">...</span>';
                for (var i = pageIndex - 3, n = pageIndex + 3; i <= n; i++) {
                    if (pageIndex == i) {
                        html += '<span class="active">' + i + '</span>';
                        continue;
                    }
                    html += '<span>' + i + '</span>';
                }
                html += '<span class="none">...</span>';
                html += '<span>' + pageTotal + '</span>';
            } else if (pageIndex > pageTotal - 7) {
                html += '<span>1</span>';
                html += '<span class="none">...</span>';
                for (var i = pageTotal - 7; i <= pageTotal; i++) {
                    if (pageIndex == i) {
                        html += '<span class="active">' + i + '</span>';
                        continue;
                    }
                    html += '<span>' + i + '</span>';
                }
            }
        }
        if (pageIndex != pageTotal) {
            html += '<span class="next">&gt;</span>';
        }
        return html;
    };
    var bindPage = function () {
        $pageBox.delegate('span', 'click', function () {
            var _this = $(this);
            if (_this.hasClass('none')) return;
            if (_this.hasClass('prev')) {
                pageIndex--;
            } else if (_this.hasClass('next')) {
                pageIndex++;
            } else {
                var index = parseInt(_this.text(), 10);
                if (pageIndex == index) return;
                pageIndex = index;
            }
            fetch();
        });
    };
    var bindSearch = function () {
        $button.click(function () {
            var query = $query.val();
            if (itemQuery != query) {
                itemQuery = query;
                pageIndex = 1;
                fetch();
            }
        });
        $query.keyup(function (e) {
            if (e.keyCode == 13) {
                var query = $query.val();
                if (itemQuery != query) {
                    itemQuery = query;
                    pageIndex = 1;
                    $query.blur();
                    fetch();
                }
            }
        });
    };
    var checkSelectAll = function () {
        var _status = true;
        $listBox.find('input').each(function (i, input) {
            if ($(input).prop('checked') == false) {
                _status = false;
                return false;
            }
        });
        if (_status) {
            $selectAll.prop('checked', true);
        } else {
            $selectAll.prop('checked', false);
        }
    };
    var bindSelect = function () {
        $selectAll.click(function (e) {
            e.stopPropagation();
            var _this = $(this);
            var _checked = _this.prop('checked');
            if (_checked) {
                $listBox.find('input').each(function (i, input) {
                    var $input = $(input);
                    $input.prop('checked', true);
                    addItem($input.val());
                });
            } else {
                $listBox.find('input').each(function (i, input) {
                    var $input = $(input);
                    $input.prop('checked', false);
                    removeItem($input.val());
                });
            }
        });
        $listBox.delegate('input', 'click', function () {
            var $input = $(this);
            var _checked = $input.prop('checked');
            if (_checked) {
                addItem($input.val());
            } else {
                removeItem($input.val());
            }
            checkSelectAll();
        });
    };
    var selector = function () {
        $listBox = $('#sp-list');
        $pageBox = $('#sp-page');
        $query = $('#sp-query');
        $button = $('#sp-button');
        $selectAll = $('#sp-selectAll');
    };
    var bind = function () {
        bindPage();
        bindSearch();
        bindSelect();
    };
    var render = function () {
        $query.val(itemQuery);
        $listBox.html(createList());
        $pageBox.html(createPage());
        checkSelectAll();
    };
    // 根据 页码 查询关键字 每页显示数量 获得商品数据 
    var fetch = function () {
        $.ajax({
            url: "/wx/getGoods",
            type: 'post',
            dataType: 'json',
            data: {
                query: itemQuery,
                nums: pageNums,
                page: pageIndex
            },
            success: function (res) {
                pageIndex = parseInt(res.data.pageIndex);
                pageTotal = parseInt(res.data.totalPage);
                pageItems = res.data.list;
                render();
            }
        });
    };
    var init = function () {
        selector();
        fetch();
        bind();
    };

    // ===================================

    // 页面中选择的参加活动商品列表
    var $itemsBox = $('#sp-items');
    var createItems = function () {
        for (var i = 0, n = spItems.length, html = ''; i < n; i++) {
            html += '<li>';
            html += '<input type="hidden" name="saleGoods[]"  value="' + spItems[i].id + '" />';
            html += '<img src="' + spItems[i].image + '" /><input type="hidden" name="image" value="' + spItems[i].image + '"> ';
            html += '<h5>' + spItems[i].title + '</h5><input type="hidden" name="title" value="' + spItems[i].title + '">';
            html += '<p><span>¥' + spItems[i].salePrice + '</span><span>库存：' + spItems[i].saleNumShow + '</span></p>' +
                '<input type="hidden" name="price" value="' + spItems[i].salePrice + '"><input type="hidden" name="num" value="' + spItems[i].saleNumShow + '">';
            html += '<b data-id="' + spItems[i].id + '">移除</b>';
            html += '</li>';
        }
        return html;
    };
    var renderItems = function () {
        if (spItems.length > 0) {
            $('.yhq-items dd').show();
            $('.yhq-button').show();
            $('#yhq-sort').show();
        }
        $itemsBox.html(createItems());
    };
    var bindItems = function () {
        $itemsBox.delegate('b', 'click', function () {
            var _this = $(this);
            var id = _this.attr('data-id');
            _this.parent('li').remove();
            removeItem(id);
        });
    };
    // 页面加载时获取 选择的商品
    var fetchItems = function () {
        $.ajax({
            url: itemsUrl,
            type: 'post',
            dataType: 'json',
            success: function (res) {
                spItems = res.data;
                renderItems();
            }
        });
    };
    var initItems = function () {
        fetchItems();
        bindItems();
    };
    initItems();

    // ===================================

    // 选择商品
    $('#sp-select').click(function (e) {
        var tempItems = spItems.concat();
        Tips.alert({
            title: '在售中商品',
            content: $('#tpl').html(),
            render: function () {
                init();
            },
            close: function () {
                spItems = tempItems.concat();
            },
            define: function () {
                renderItems();
            }
        });
    });
}