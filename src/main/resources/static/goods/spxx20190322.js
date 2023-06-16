// function addItem(item) {
// 	var allItems = document.querySelectorAll('.cp-item');
// 	if (!allItems.length) {
// 		// 禁用总库存输入
// 		disableStock();
// 	}
// 	var html = '<input type="text" name="color[]" placeholder="请输入颜色">\
// 						<input type="text" name="size[]" placeholder="请输入尺码">\
// 						<input type="text" name="price[]" placeholder="请输入价格">\
// 						<input type="text" class="cp-stock" onblur="updateStock()" name="stock[]" placeholder="请输入库存">\
// 						<input type="text" name="code[]" placeholder="请输入条形码">\
// 						<span><img><input type="file" onchange="addImage(this)" name="image[]"><i>上传图片</i></span>\
// 						<b onclick="removeItem(this)">×</b>';
// 	var o = item.parentNode;
// 	var box = document.createElement('div');
// 	box.className = 'cp-item';
// 	box.innerHTML = html;
// 	o.appendChild(box);
// }

// function removeItem(item) {
// 	var o = item.parentNode;
// 	o.parentNode.removeChild(o);
// 	o = null;
// 	var allItems = document.querySelectorAll('.cp-item');
// 	if (!allItems.length) {
// 		// 启用总库存输入
// 		enableStock();
// 	} else {
// 		// 更新总库存
// 		updateStock();

// 	}
// }

function addImage(file) {
    var fd = new FileReader();
    var img = file.previousSibling;
    fd.readAsDataURL(file.files[0]);
    fd.onload = function (e) {
        img.src = this.result;
        img.style.display = 'block';
    };
}

// function enableStock() {
// 	var allStock = document.querySelector('#all-stock');
// 	allStock.removeAttribute('readonly', 'readonly');
// 	allStock.style.cssText = '';
// 	allStock.value = '';
// }

// function disableStock() {
// 	var allStock = document.querySelector('#all-stock');
// 	allStock.setAttribute('readonly', 'readonly');
// 	allStock.style.backgroundColor = '#f2f2f2';
// 	allStock.value = '';
// }

// function updateStock() {
// 	var allStock = document.querySelector('#all-stock');
// 	var allInputs = document.querySelectorAll('.cp-stock');
// 	for (var i = 0, n = allInputs.length, input = null, total = 0; i < n; i++) {
// 		input = allInputs[i];
// 		if (input.value == '' || parseInt(input.value) < 1) continue;
// 		total += parseInt(input.value);
// 	}
// 	total = parseInt(total);
// 	allStock.value = total > 0 ? total : '';
// }

function addVideo(file) {
    var fd = new FileReader();
    var video = file.previousSibling;
    fd.readAsDataURL(file.files[0]);
    fd.onload = function (e) {
        video.src = this.result;
        video.style.display = 'block';
    };
}