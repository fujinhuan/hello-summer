function addItem(item) {
    var allItems = document.querySelectorAll('.cp-item');
    if (!allItems.length) {
        // 禁用总库存输入
        disableStock();
    }
    var html = '<input type="text" name="spec_color[]" placeholder="请输入颜色">\
						<input type="text" name="spec_size[]" placeholder="请输入尺码">\
						<input type="number" name="spec_price[]" placeholder="请输入价格">\
						<input type="number" class="cp-stock" onblur="updateStock()" name="spec_stock[]" placeholder="请输入库存">\
						<input type="text" name="spec_code[]" placeholder="请输入条形码">\
						<span><img><input type="file" onchange="addImage(this)" ><input type="hidden" name="spec_image[]"/><i>上传图片</i></span>\
                                <b onclick="removeItem(this)">×</b>';
    var o = item.parentNode;
    var box = document.createElement('div');
    box.className = 'cp-item';
    box.innerHTML = html;
    o.appendChild(box);
}

function removeItem(item) {
    var o = item.parentNode;
    o.parentNode.removeChild(o);
    o = null;
    var allItems = document.querySelectorAll('.cp-item');
    if (!allItems.length) {
        // 启用总库存输入
        enableStock();
    } else {
        // 更新总库存
        updateStock();

    }
}

function addImage(file) {
    var fd = new FileReader();
    var img = file.previousSibling;
    var imgInput = file.nextSibling;
    fd.readAsDataURL(file.files[0]);
    fd.onload = function (e) {
        img.src = this.result;
        img.style.display = 'block';
        $.ajax({
            url: "/upload_image_base64",
            type: "POST",
            data: {image: this.result},
            // processData: false,
            // contentType: false,
            // cache: false,
            dataType: "json",
            success: function (res) {
                if (res.code == 0) {
                    imgInput.value = res.data.src;
                } else {
                    alert(res.msg);
                }
                // alert(JSON.stringify(res));

            }
        })
        // imgInput.value =this.result;
    };
}

function enableStock() {
    var allStock = document.querySelector('#all-stock');
    allStock.removeAttribute('readonly', 'readonly');
    allStock.style.cssText = '';
    allStock.value = '';
    document.querySelector('#price_div').style.display = "block";
}

function disableStock() {
    var allStock = document.querySelector('#all-stock');
    allStock.setAttribute('readonly', 'readonly');
    allStock.style.backgroundColor = '#f2f2f2';
    allStock.value = '';
    document.querySelector('#price_div').style.display = "none";
}

function updateStock() {
    var allStock = document.querySelector('#all-stock');
    var allInputs = document.querySelectorAll('.cp-stock');
    for (var i = 0, n = allInputs.length, input = null, total = 0; i < n; i++) {
        input = allInputs[i];
        if (input.value == '' || parseInt(input.value) < 1) continue;
        total += parseInt(input.value);
    }
    total = parseInt(total);
    allStock.value = total > 0 ? total : '';
}