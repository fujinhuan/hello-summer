$(function() {
    var $cart = $('.cart-box');
    var $button = $('#sub_btn'); // 结算按钮
    var $pddButton = $('#pdd_sub_btn'); // 结算按钮
    var $goodnum=$('#good_num');//数量
    // 遍历商品
    function each(cb) {
        $('.item').each(function(i, item) {
            var $item = $(item);
            var $label = $item.find('label');
            if (!$label.length) return true;
            var $check = $label.find('input[type=checkbox]');
            if (!$check.length) return true;
            var $input=$item.find('input[type=text]');
            cb && cb($item, $label, $check,$input);
        });
    }

    // 是否有选中
    function $checkOne() {
        var status = false;
        $('.i-single').each(function(i, el) {
            if ($(this).hasClass('active')) {
                status = true;
                return false;
            }
        });
        return status;
    }
    // 切换按钮背景
    function $toggle() {
        if ($checkOne()) {
            $button.addClass('active');
        } else {
            $button.removeClass('active');
        }
    }

    // 切换按钮背景
    function $pdd_toggle() {
        if ($checkOne()) {
            $pddButton.addClass('active');
        } else {
            $pddButton.removeClass('active');
        }
    }
    // 单个选择
    $cart.delegate('.i-single', 'click', function(e) {
        e.stopPropagation();
        e.preventDefault();
        var $label = $(this);
        var $check = $label.find('input[type=checkbox]');
        if ($label.hasClass('active')) {
            $label.removeClass('active');
            $check.prop('checked', false);
        } else {
            $label.addClass('active');
            $check.prop('checked', true);
        }
        $toggle();
        $pdd_toggle();
        addup();
    });
    // 全部选择
    $cart.delegate('.i-select', 'click', function(e) {
        e.stopPropagation();
        var $select = $(this);
        if ($select.hasClass('active')) {
            $select.removeClass('active');
            $cart.find('label').each(function(i, label) {
                var $label = $(label);
                $label.removeClass('active');
                $label.find('input').prop('checked', false);
            });
        } else {
            $select.addClass('active');
            $cart.find('label').each(function(i, label) {
                var $label = $(label);
                $label.addClass('active');
                $label.find('input').prop('checked', true);
            });
        }
        $toggle();
        $pdd_toggle();
        addup();
    });

    // 计算总额
    function addup() {
        var res = 0;
        var num=0;
        each(function($item, $label, $check,$input) {
            var vals = $check.val().split(','); // 1001,红色,M,129,1
            console.log(vals);
            var price = vals[3]; // 单价
            var count = $input.val();//vals[4]; // 件数
            var stock = vals[5]; // 库存
            if ($check.prop('checked') === true) {
                if (stock > 0) res += price * count;
                num+=$check.length;
            }
        });
        $goodnum.text(num);
    }
    // 点击结算按钮
    $button.click(function(e) {
        e.preventDefault();
        if (!$button.hasClass('active')) return;
        var res = false;
        each(function($item, $label, $check) {
            if ($item.hasClass('disable')) return true;
            if ($check.prop('checked') === true) res = true;
        });
        if (!res) {
            Tips.alert('请选择要结算的物品');
        } else {
            $.ajax({
                type: "POST",
                dataType: "json",
                processData: false,
                url: "/order_yungou/refund_apply_submit" ,
                data: $('#cart_form').serialize(),
                success: function (result) {
                    // console.log(result);//打印服务端返回的数据(调试用)
                    var shopId= $("#shopId").val();
                    if (result.code == 0) {
                        Tips.alert('操作成功', function() {
                            location.href='/order_yungou/refund_list?shopId='+shopId;
                        });
                    }else{
                        Tips.alert(result.msg);
                    }
                },
                error : function() {
                    Tips.alert("异常！");
                }
            });
        }
    });
    $pddButton.click(function(e) {
        e.preventDefault();
        if (!$pddButton.hasClass('active')) return;
        var res = false;
        each(function($item, $label, $check) {
            if ($item.hasClass('disable')) return true;
            if ($check.prop('checked') === true) res = true;
        });
        if (!res) {
            Tips.alert('请选择要结算的物品');
        } else {
            $.ajax({
                type: "POST",
                dataType: "json",
                processData: false,
                url: "/ajax_pdd/refund_apply_pdd_submit" ,
                data: $('#cart_form').serialize(),
                success: function (result) {
                    // console.log(result);//打印服务端返回的数据(调试用)
                    var shopId= $("#shopId").val();
                    if (result.code == 0) {
                        Tips.alert('操作成功', function() {
                            location.href='/order_yungou/refund_list?shopId='+shopId;
                        });
                    }else{
                        Tips.alert(result.msg);
                    }
                },
                error : function() {
                    Tips.alert("异常！");
                }
            });
        }
    });
});