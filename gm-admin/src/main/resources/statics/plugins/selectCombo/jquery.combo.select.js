(function (factory) {
    'use strict';
    if (typeof define === 'function' && define.amd) {
        define(['jquery'], factory);
    } else if (typeof exports === 'object' && typeof require === 'function') {
        factory(require('jquery'));
    } else {
        factory(jQuery);
    }
}(function ($, undefined) {
    var pluginName = "comboSelect", dataKey = 'comboselect';
    var defaults = {
        comboClass: 'combo-select',
        comboArrowClass: 'combo-arrow',
        comboDropDownClass: 'combo-dropdown',
        inputClass: 'combo-input text-input',
        disabledClass: 'option-disabled',
        hoverClass: 'option-hover',
        selectedClass: 'option-selected',
        markerClass: 'combo-marker',
        themeClass: '',
        maxHeight: 200,
        extendStyle: true,
        focusInput: true
    };
    var keys = {ESC: 27, TAB: 9, RETURN: 13, LEFT: 37, UP: 38, RIGHT: 39, DOWN: 40, ENTER: 13, SHIFT: 16},
        isMobile = (/android|webos|iphone|ipad|ipod|blackberry|iemobile|opera mini/i.test(navigator.userAgent.toLowerCase()));

    function Plugin(element, options) {
        this._name = pluginName;
        this.el = element
        this.$el = $(element)
        if (this.$el.prop('multiple'))return;
        this.settings = $.extend({}, defaults, options, this.$el.data());
        this._defaults = defaults;
        this.$options = this.$el.find('option, optgroup')
        this.init();
        $.fn[pluginName].instances.push(this);
    }

    $.extend(Plugin.prototype, {
        init: function () {
            this._construct();
            this._events();
        }, _construct: function () {
            var self = this
            this.$el.data('plugin_' + dataKey + '_tabindex', this.$el.prop('tabindex'))
            !isMobile && this.$el.prop("tabIndex", -1)
            this.$container = this.$el.wrapAll('<div class="' + this.settings.comboClass + ' ' + this.settings.themeClass + '" />').parent();
            if (this.settings.extendStyle && this.$el.attr('style')) {
                this.$container.attr('style', this.$el.attr("style"))
            }
            this.$arrow = $('<div class="' + this.settings.comboArrowClass + '" />').appendTo(this.$container)
            this.$dropdown = $('<ul class="' + this.settings.comboDropDownClass + '" />').appendTo(this.$container)
            var o = '', k = 0, p = '';
            this.selectedIndex = this.$el.prop('selectedIndex')
            this.$options.each(function (i, e) {
                if (e.nodeName.toLowerCase() == 'optgroup') {
                    return o += '<li class="option-group">' + this.label + '</li>'
                }
                var mapCodes = e.dataset;
                if (!e.value) p = e.innerHTML
                o += '<li class="' + (this.disabled ? self.settings.disabledClass : "option-item") + ' ' + (k == self.selectedIndex ? self.settings.selectedClass : '') + '" data-index="' + (k) + '" data-value="' + this.value + '" data-code="' + mapCodes.code + '" data-code2="' + mapCodes.code2 + '">' + (this.innerHTML) + '</li>'
                k++;
            })
            this.$dropdown.html(o)
            this.$items = this.$dropdown.children();
            this.$input = $('<input type="text"' + (isMobile ? 'tabindex="-1"' : '') + ' placeholder="' + p + '" class="' + this.settings.inputClass + '">').appendTo(this.$container)
            this._updateInput()
        }, _events: function () {
            this.$container.on('focus.input', 'input', $.proxy(this._focus, this))
            this.$container.on('mouseup.input', 'input', function (e) {
                e.preventDefault()
            })
            this.$container.on('blur.input', 'input', $.proxy(this._blur, this))
            this.$el.on('change.select', $.proxy(this._change, this))
            this.$el.on('focus.select', $.proxy(this._focus, this))
            this.$el.on('blur.select', $.proxy(this._blurSelect, this))
            this.$container.on('click.arrow', '.' + this.settings.comboArrowClass, $.proxy(this._toggle, this))
            this.$container.on('comboselect:close', $.proxy(this._close, this))
            this.$container.on('comboselect:open', $.proxy(this._open, this))
            $('html').off('click.comboselect').on('click.comboselect', function () {
                $.each($.fn[pluginName].instances, function (i, plugin) {
                    plugin.$container.trigger('comboselect:close')
                })
            });
            this.$container.on('click.comboselect', function (e) {
                e.stopPropagation();
            })
            this.$container.on('keydown', 'input', $.proxy(this._keydown, this))
            this.$container.on('keyup', 'input', $.proxy(this._keyup, this))
            this.$container.on('click.item', '.option-item', $.proxy(this._select, this))
        }, _keydown: function (event) {
            switch (event.which) {
                case keys.UP:
                    this._move('up', event)
                    break;
                case keys.DOWN:
                    this._move('down', event)
                    break;
                case keys.TAB:
                    this._enter(event)
                    break;
                case keys.RIGHT:
                    this._autofill(event);
                    break;
                case keys.ENTER:
                    this._enter(event);
                    break;
                default:
                    break;
            }
        }, _keyup: function (event) {
            switch (event.which) {
                case keys.ESC:
                    this.$container.trigger('comboselect:close')
                    break;
                case keys.ENTER:
                case keys.UP:
                case keys.DOWN:
                case keys.LEFT:
                case keys.RIGHT:
                case keys.TAB:
                case keys.SHIFT:
                    break;
                default:
                    this._filter(event.target.value)
                    break;
            }
        }, _enter: function (event) {
            var item = this._getHovered()
            item.length && this._select(item);
            if (event && event.which == keys.ENTER) {
                if (!item.length) {
                    this._blur();
                    return true;
                }
                event.preventDefault();
            }
        }, _move: function (dir) {
            var items = this._getVisible(), current = this._getHovered(),
                index = current.prevAll('.option-item').filter(':visible').length, total = items.length
            switch (dir) {
                case 'up':
                    index--;
                    (index < 0) && (index = (total - 1));
                    break;
                case 'down':
                    index++;
                    (index >= total) && (index = 0);
                    break;
            }
            items.removeClass(this.settings.hoverClass).eq(index).addClass(this.settings.hoverClass)
            if (!this.opened) this.$container.trigger('comboselect:open');
            this._fixScroll()
        }, _select: function (event) {
            var item = event.currentTarget ? $(event.currentTarget) : $(event);
            if (!item.length)return;
            var index = item.data('index');
            this._selectByIndex(index);
            this.$container.trigger('comboselect:close')
        }, _selectByIndex: function (index) {
            if (typeof index == 'undefined') {
                index = 0
            }
            if (this.$el.prop('selectedIndex') != index) {
                this.$el.prop('selectedIndex', index).trigger('change');
            }
        }, _autofill: function () {
            var item = this._getHovered();
            if (item.length) {
                var index = item.data('index')
                this._selectByIndex(index)
            }
        }, _filter: function (search) {
            var self = this, items = this._getAll();
            needle = $.trim(search).toLowerCase(), reEscape = new RegExp('(\\' + ['/', '.', '*', '+', '?', '|', '(', ')', '[', ']', '{', '}', '\\'].join('|\\') + ')', 'g'), pattern = '(' + search.replace(reEscape, '\\$1') + ')';
            $('.' + self.settings.markerClass, items).contents().unwrap();
            if (needle) {
                this.$items.filter('.option-group, .option-disabled').hide();
                items.hide().filter(function () {
                    var $this = $(this), text = $.trim($this.text()).toLowerCase();
                    if (text.toString().indexOf(needle) != -1) {
                        $this.html(function (index, oldhtml) {
                            return oldhtml.replace(new RegExp(pattern, 'gi'), '<span class="' + self.settings.markerClass + '">$1</span>')
                        })
                        return true
                    }
                }).show()
            } else {
                this.$items.show();
            }
            this.$container.trigger('comboselect:open')
        }, _highlight: function () {
            var visible = this._getVisible().removeClass(this.settings.hoverClass),
                $selected = visible.filter('.' + this.settings.selectedClass)
            if ($selected.length) {
                $selected.addClass(this.settings.hoverClass);
            } else {
                visible.removeClass(this.settings.hoverClass).first().addClass(this.settings.hoverClass)
            }
        }, _updateInput: function () {
            var selected = this.$el.prop('selectedIndex')
            if (this.$el.val()) {
                text = this.$el.find('option').eq(selected).text()
                this.$input.val(text)
            } else {
                this.$input.val('')
            }
            return this._getAll().removeClass(this.settings.selectedClass).filter(function () {
                return $(this).data('index') == selected
            }).addClass(this.settings.selectedClass)
        }, _blurSelect: function () {
            this.$container.removeClass('combo-focus');
        }, _focus: function (event) {
            this.$container.toggleClass('combo-focus', !this.opened);
            if (isMobile)return;
            if (!this.opened) this.$container.trigger('comboselect:open');
            this.settings.focusInput && event && event.currentTarget && event.currentTarget.nodeName == 'INPUT' && event.currentTarget.select()
        }, _blur: function () {
            var val = $.trim(this.$input.val().toLowerCase()), isNumber = !isNaN(val);
            var index = this.$options.filter(function () {
                if (isNumber) {
                    return parseInt($.trim(this.innerHTML).toLowerCase()) == val
                }
                return $.trim(this.innerHTML).toLowerCase() == val
            }).prop('index')
            this._selectByIndex(index)
        }, _change: function () {
            this._updateInput();
        }, _getAll: function () {
            return this.$items.filter('.option-item')
        }, _getVisible: function () {
            return this.$items.filter('.option-item').filter(':visible')
        }, _getHovered: function () {
            return this._getVisible().filter('.' + this.settings.hoverClass);
        }, _open: function () {
            var self = this
            this.$container.addClass('combo-open')
            this.opened = true
            this.settings.focusInput && setTimeout(function () {
                !self.$input.is(':focus') && self.$input.focus();
            });
            this._highlight()
            this._fixScroll()
            $.each($.fn[pluginName].instances, function (i, plugin) {
                if (plugin != self && plugin.opened) plugin.$container.trigger('comboselect:close')
            })
        }, _toggle: function () {
            this.opened ? this._close.call(this) : this._open.call(this)
        }, _close: function () {
            this.$container.removeClass('combo-open combo-focus')
            this.$container.trigger('comboselect:closed')
            this.opened = false
            this.$items.show();
        }, _fixScroll: function () {
            if (this.$dropdown.is(':hidden'))return;
            var item = this._getHovered();
            if (!item.length)return;
            var offsetTop, upperBound, lowerBound, heightDelta = item.outerHeight()
            offsetTop = item[0].offsetTop;
            upperBound = this.$dropdown.scrollTop();
            lowerBound = upperBound + this.settings.maxHeight - heightDelta;
            if (offsetTop < upperBound) {
                this.$dropdown.scrollTop(offsetTop);
            } else if (offsetTop > lowerBound) {
                this.$dropdown.scrollTop(offsetTop - this.settings.maxHeight + heightDelta);
            }
        }, dispose: function () {
            this.$arrow.remove()
            this.$input.remove()
            this.$dropdown.remove()
            this.$el.removeAttr("tabindex")
            if (!!this.$el.data('plugin_' + dataKey + '_tabindex')) {
                this.$el.prop('tabindex', this.$el.data('plugin_' + dataKey + '_tabindex'))
            }
            this.$el.unwrap()
            this.$el.removeData('plugin_' + dataKey)
            this.$el.removeData('plugin_' + dataKey + '_tabindex')
            this.$el.off('change.select focus.select blur.select');
        }
    });
    $.fn[pluginName] = function (options, args) {
        this.each(function () {
            var $e = $(this), instance = $e.data('plugin_' + dataKey)
            if (typeof options === 'string') {
                if (instance && typeof instance[options] === 'function') {
                    instance[options](args);
                }
            } else {
                if (instance && instance.dispose) {
                    instance.dispose();
                }
                $.data(this, "plugin_" + dataKey, new Plugin(this, options));
            }
        });
        return this;
    };
    $.fn[pluginName].instances = [];
}));