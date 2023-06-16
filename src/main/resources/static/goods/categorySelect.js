/**
 * LBS categorySelect
 * Date: 2018-12-20
 * ===================================================
 * *选项 属性*
 * opts.data 数据(必须设置)
 * opts.parent UI插入到哪里 默认 body
 * *选项 方法*
 * opts.select 每次选择类别后执行函数 返回类别文本
 * opts.complete 选择最后一项的类别后执行函数 返回类别编码
 * ===================================================
 **/
(function (global, factory) {
    typeof exports === 'object' && typeof module !== 'undefined' ? module.exports = factory() :
        typeof define === 'function' && (define.amd || define.cmd) ? define(factory) :
            (global.categorySelect = factory());
}(this, (function () {
    'use strict';

    var addEvent = function (el, type, handler) {
        if (el.addEventListener) {
            el.addEventListener(type, handler, false);
        } else if (el.attachEvent) {
            el.attachEvent('on' + type, handler);
        } else {
            el['on' + type] = handler;
        }
    };

    var categorySelect = function (opts) {
        opts = opts || {};
        this.data = opts.data;
        if (!this.data) return;
        this.parent = opts.parent || document.body;
        this.select = opts.select || function () {
        };
        this.complete = opts.complete || function () {
        };
        this.category = {
            s1: null,
            s2: null,
            s3: null,
        };
        this._init();
    };
    categorySelect.prototype = {
        _init: function () {
            this._initCreate();
            this._bindEvent();
        },
        _initCreate: function () {
            var html = '<ul class="category-s1"></ul><ul class="category-s2"></ul><ul class="category-s3"></ul>';
            this.$box = document.createElement('div');
            this.$box.className = 'category-box';
            this.$box.innerHTML = html;
            this.parent.appendChild(this.$box);

            this.$one = document.querySelector('.category-s1');
            this.$two = document.querySelector('.category-s2');
            this.$three = document.querySelector('.category-s3');

            this._create(this.$one, '88', 1);
        },
        _create: function (dom, code, level, text) {
            dom.innerHTML = '';
            var data = this.data[code];
            if (!data) return;
            var pcode = code;
            var level = level;
            var active = 'class="active"';
            var k, html = '';
            for (k in data) {
                html += '<li ' + (data[k] === text ? active : '') + ' data-code="' + k + '" data-pcode="' + pcode + '" data-level="' + level + '">' + data[k] + '</li>';
            }
            dom.innerHTML = html;
        },
        _bindEvent: function () {
            addEvent(this.$box, 'click', function (e) {
                e.stopPropagation();
                var target = e.target;
                var level = 0;
                var pcode = -1;
                var code = -1;
                var text = '';
                var data = null;
                if (target.tagName.toUpperCase() === 'LI') {
                    level = parseInt(target.getAttribute('data-level'));
                    pcode = target.getAttribute('data-pcode');
                    code = target.getAttribute('data-code');
                    text = target.innerHTML;
                    data = this.data[code];
                    switch (level) {
                        case 1:
                            this.category.s1 = [code, text, 1, pcode];
                            this.category.s2 = null;
                            this.category.s3 = null;
                            this.$two.innerHTML = '';
                            this.$three.innerHTML = '';
                            this._create(this.$one, pcode, 1, text);
                            this._create(this.$two, code, 2, text);
                            break;
                        case 2:
                            this.category.s2 = [code, text, 2, pcode];
                            this.category.s3 = null;
                            this.$three.innerHTML = '';
                            this._create(this.$two, pcode, 2, text);
                            this._create(this.$three, code, 3, text);
                            break;
                        case 3:
                            this.category.s3 = [code, text, 3, pcode];
                            this._create(this.$three, pcode, 3, text);
                            break;
                    }
                    this._select();
                    if (!data) {
                        this._selectEnd();
                    }
                }
            }.bind(this));
        },
        _getText: function () {
            var res = [];
            if (this.category.s1 !== null) res[0] = this.category.s1[1];
            if (this.category.s2 !== null) res[1] = this.category.s2[1];
            if (this.category.s3 !== null) res[2] = this.category.s3[1];
            return res;
        },
        _getCode: function () {
            var res = [];
            if (this.category.s1 !== null) res[0] = this.category.s1[0];
            if (this.category.s2 !== null) res[1] = this.category.s2[0];
            if (this.category.s3 !== null) res[2] = this.category.s3[0];
            return res;
        },
        _select: function () {
            this.select && this.select(this._getText());
        },
        _selectEnd: function () {
            this.complete && this.complete(this._getCode());
        }
    };

    return categorySelect;
})));