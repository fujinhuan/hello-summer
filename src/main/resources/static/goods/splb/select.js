/**
 * LBS Select
 * Date: 2019-1-22
 **/
(function (global, factory) {
    typeof exports === 'object' && typeof module !== 'undefined' ? module.exports = factory() :
        typeof define === 'function' && (define.amd || define.cmd) ? define(factory) :
            (global.Select = factory());
}(this, (function () {
    'use strict';

    var utils = (function () {
        var on = function (el, type, fn) {
            if (typeof type === 'string') return el.addEventListener(type, fn, false);
            for (var i = 0, l = type.length; i < l; i++) el.addEventListener(type[i], fn, false);
        };

        var hasClass = function (el, cls) {
            return -1 < (' ' + el.className + ' ').indexOf(' ' + cls + ' ');
        };

        var addClass = function (el, cls) {
            if (!hasClass(el, cls)) el.className += ((el.className === '' ? '' : ' ') + cls);
        };

        var removeClass = function (el, cls) {
            if (hasClass(el, cls)) {
                var reg = new RegExp('(\\s|^)' + cls + '(\\s|$)');
                el.className = el.className.replace(reg, '');
            }
        };

        var indexOf = function (item, array) {
            var res = false,
                i = 0,
                n = array.length;
            for (; i < n; i++) {
                if (item === array[i]) {
                    res = true;
                    break;
                }
            }
            return res;
        };

        return {
            on: on,
            hasClass: hasClass,
            addClass: addClass,
            removeClass: removeClass,
            indexOf: indexOf
        };
    }());

    var Select = function (opts) {
        opts = opts || {};
        this.data = opts.data;
        if (!this.data || this.data.length < 1) return;
        this.$box = opts.box;
        this.$color = opts.color;
        this.$size = opts.size;
        this.$price = opts.price;
        this.$stock = opts.stock;
        this.colors = [];
        this.sizes = [];
        this.result = {
            color: null,
            size: null
        };
        this._init();
    };
    Select.prototype = {
        _init: function () {
            this._getUnique();
            this._initSet();
            this._initEvent();
        },
        _initSet: function () {
            this._createColor();
            this._createSize();
        },
        _createItem: function (array, item, type, array2) {
            var html = '';
            var i = 0,
                n = array.length,
                type, value,
                status = !!(array2 && array2.length > 0);
            for (; i < n; i++) {
                value = array[i];
                if (status && !utils.indexOf(value, array2)) {
                    html += '<span class="disable" data-type="' + type + '" data-value="' + value + '">' + value + '</span>';
                    continue;
                }
                html += '<span' + (value === item ? ' class="active"' : '') + ' data-type="' + type + '" data-value="' + value + '">' + value + '</span>';
            }
            return html;
        },
        _createColor: function (item, array) {
            this.$color.innerHTML = this._createItem(this.colors, item, 'color', array);
        },
        _createSize: function (item, array) {
            this.$size.innerHTML = this._createItem(this.sizes, item, 'size', array);
        },
        _setPrice: function (value) {
            this.$price.innerHTML = value;
        },
        _setStock: function (value) {
            this.$stock.innerHTML = value;
        },
        _initEvent: function () {
            utils.on(this.$box, 'click', function (e) {
                e.stopPropagation();
                var target = e.target;
                var type = '';
                var value = '';
                var res = null;
                if (utils.hasClass(target, 'disable')) return;
                if (target.tagName.toUpperCase() === 'SPAN') {
                    type = target.getAttribute('data-type');
                    value = target.getAttribute('data-value');

                    if (type === 'color') {
                        this.result.color = value;
                        if (utils.hasClass(target, 'active')) {
                            this.result.color = null;
                        }
                        this._createColor(this.result.color);
                        if (this.result.size !== null) {
                            this._createColor(this.result.color, this._getResult(this.result.size, 'size'));
                        }
                        this._createSize(this.result.size, this._getResult(this.result.color, 'color'));
                    }

                    if (type === 'size') {
                        this.result.size = value;
                        if (utils.hasClass(target, 'active')) {
                            this.result.size = null;
                        }
                        this._createSize(this.result.size);
                        if (this.result.color !== null) {
                            this._createSize(this.result.size, this._getResult(this.result.color, 'color'));
                        }
                        this._createColor(this.result.color, this._getResult(this.result.size, 'size'));
                    }

                    this._setPrice('');
                    this._setStock('');
                    if (this.result.color !== null && this.result.size !== null) {
                        res = this._getItem(this.result.color, this.result.size);
                        this._setPrice(res.price);
                        this._setStock(res.stock);
                    }
                }
            }.bind(this));
        },
        _getResult: function (item, type) {
            var array = this.data,
                res = [],
                i = 0,
                n = array.length;
            for (; i < n; i++) {
                // if (parseInt(array[i].stock) < 1) continue;
                if (type === 'color' && item === array[i].color) {
                    res.push(array[i].size);
                } else if (type === 'size' && item === array[i].size) {
                    res.push(array[i].color);
                }
            }
            return res;
        },
        _getItem: function (color, size) {
            var array = this.data,
                res = null,
                i = 0,
                n = array.length;
            for (; i < n; i++) {
                if (color === array[i].color && size === array[i].size) {
                    res = array[i];
                    break;
                }
            }
            return res;
        },
        _getUnique: function () {
            var data = this.data;
            var pcolor = '';
            var psize = '';
            for (var i = 0, n = data.length; i < n; i++) {
                pcolor = data[i].color;
                if (!utils.indexOf(pcolor, this.colors)) {
                    this.colors.push(pcolor);
                }

                psize = data[i].size;
                if (!utils.indexOf(psize, this.sizes)) {
                    this.sizes.push(psize);
                }
            }
        }
    };

    return Select;
})));