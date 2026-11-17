package com.dzaitsev.rxviper

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertNotSame
import kotlin.test.assertSame
import kotlin.test.assertTrue

class PresenterTest {
    interface Screen : ViewCallbacks {
        fun show(value: String)
    }

    interface Navigation : Router {
        fun go(value: String)
    }

    interface InvalidScreen : ViewCallbacks {
        val title: String
    }

    interface InvalidSuspendScreen : ViewCallbacks {
        suspend fun refresh()
    }

    interface GenericScreen<T> : ViewCallbacks {
        fun show(value: T)
    }

    interface StringScreen : GenericScreen<String>

    class RecordingScreen(val values: MutableList<String> = mutableListOf()) : Screen {
        override fun show(value: String) {
            values += value
        }
    }

    class RecordingNavigation(val values: MutableList<String> = mutableListOf()) : Navigation {
        override fun go(value: String) {
            values += value
        }
    }

    open class TestPresenter(initialView: Screen? = null, initialRouter: Navigation? = null) :
        ViperPresenter<Screen, Navigation>(Screen::class.java, Navigation::class.java, initialView, initialRouter) {
        val events = mutableListOf<String>()
        val exposedView: Screen get() = view
        val exposedRouter: Navigation get() = router
        var dropAction: (() -> Unit)? = null
        var routerDropAction: (() -> Unit)? = null

        override fun onTakeView(view: Screen) {
            assertTrue(hasView())
            this.view.show("take")
            events += "takeView"
        }

        override fun onDropView(view: Screen) {
            assertTrue(hasView())
            this.view.show("drop")
            events += "dropView"
            dropAction?.invoke()
        }

        override fun onTakeRouter(router: Navigation) {
            assertTrue(hasRouter())
            this.router.go("take")
            events += "takeRouter"
        }

        override fun onDropRouter(router: Navigation) {
            assertTrue(hasRouter())
            this.router.go("drop")
            events += "dropRouter"
            routerDropAction?.invoke()
        }
    }

    @Test
    fun `detached callbacks are no ops with stable proxy identity`() {
        val presenter = TestPresenter()
        val proxy = presenter.exposedView
        assertFalse(presenter.hasView())
        proxy.show("ignored")
        assertSame(proxy, presenter.exposedView)
        assertTrue(proxy == proxy)
        assertFalse(proxy.equals(null))
        assertFalse(proxy.equals(Any()))
        assertEquals(System.identityHashCode(proxy), proxy.hashCode())
        assertTrue(proxy.toString().contains("detached"))
    }

    @Test
    fun `attachment calls hooks once and replacement drops old first`() {
        val presenter = TestPresenter()
        val first = RecordingScreen()
        val second = RecordingScreen()
        presenter.takeView(first)
        presenter.takeView(first)
        presenter.takeView(second)
        presenter.dropView(first)
        presenter.exposedView.show("live")
        assertEquals(listOf("takeView", "dropView", "takeView"), presenter.events)
        assertEquals(listOf("take", "drop"), first.values)
        assertEquals(listOf("take", "live"), second.values)
        presenter.dropView(second)
        presenter.exposedView.show("ignored")
        assertFalse(presenter.hasView())
        assertEquals(listOf("take", "live", "drop"), second.values)
    }

    @Test
    fun `preinitialization does not call overridable hooks`() {
        val screen = RecordingScreen()
        val navigation = RecordingNavigation()
        val presenter = TestPresenter(screen, navigation)
        assertTrue(presenter.hasView())
        assertTrue(presenter.hasRouter())
        assertTrue(presenter.events.isEmpty())
        presenter.exposedView.show("ready")
        presenter.exposedRouter.go("ready")
        assertEquals(listOf("ready"), screen.values)
        assertEquals(listOf("ready"), navigation.values)
    }

    @Test
    fun `router lifecycle is independent and ignores duplicate takes and stale drops`() {
        val presenter = TestPresenter()
        val first = RecordingNavigation()
        val second = RecordingNavigation()
        presenter.takeRouter(first)
        presenter.takeRouter(first)
        presenter.takeRouter(second)
        presenter.dropRouter(first)
        assertTrue(presenter.hasRouter())
        assertFalse(presenter.hasView())
        assertEquals(listOf("takeRouter", "dropRouter", "takeRouter"), presenter.events)
        assertEquals(listOf("take", "drop"), first.values)
        presenter.dropRouter(second)
        presenter.exposedRouter.go("ignored")
        assertFalse(presenter.hasRouter())
        assertEquals(listOf("take", "drop"), second.values)
    }

    @Test
    fun `throwing drop hooks still detach`() {
        val screen = RecordingScreen()
        val navigation = RecordingNavigation()
        val presenter = TestPresenter(screen, navigation)
        presenter.dropAction = { error("drop view") }
        presenter.routerDropAction = { error("drop router") }
        assertFailsWith<IllegalStateException> { presenter.dropView(screen) }
        assertFailsWith<IllegalStateException> { presenter.dropRouter(navigation) }
        assertFalse(presenter.hasView())
        assertFalse(presenter.hasRouter())
    }

    @Test
    fun `reentrant attachment survives drop cleanup and outer replacement`() {
        val first = RecordingScreen()
        val nested = RecordingScreen()
        val outer = RecordingScreen()
        val presenter = TestPresenter(first)
        presenter.dropAction = { presenter.takeView(nested) }
        presenter.takeView(outer)
        presenter.exposedView.show("nested wins")
        assertEquals(listOf("take", "nested wins"), nested.values)
        assertTrue(outer.values.isEmpty())
        assertTrue(presenter.hasView())
    }

    @Test
    fun `reentrant router attachment survives drop cleanup`() {
        val first = RecordingNavigation()
        val nested = RecordingNavigation()
        val presenter = TestPresenter(initialRouter = first)
        presenter.routerDropAction = { presenter.takeRouter(nested) }
        presenter.dropRouter(first)
        presenter.exposedRouter.go("nested")
        assertEquals(listOf("take", "nested"), nested.values)
        assertTrue(presenter.hasRouter())
    }

    @Test
    fun `proxy object methods retain identity while attached`() {
        val screen = RecordingScreen()
        val presenter = TestPresenter(screen)
        val proxy = presenter.exposedView
        assertNotSame(screen, proxy)
        assertNotEquals(screen as Any, proxy as Any)
        assertEquals(System.identityHashCode(proxy), proxy.hashCode())
        assertTrue(proxy.toString().contains("attached"))
        val identityHash = proxy.hashCode()
        presenter.dropView(screen)
        assertEquals(identityHash, proxy.hashCode())
    }

    @Test
    fun `invalid callback contracts fail when presenter is created`() {
        assertFailsWith<IllegalArgumentException> {
            object : Presenter<InvalidScreen>(InvalidScreen::class.java) {}
        }
        assertFailsWith<IllegalArgumentException> {
            object : Presenter<InvalidSuspendScreen>(InvalidSuspendScreen::class.java) {}
        }
        assertFailsWith<IllegalArgumentException> {
            object : Presenter<RecordingScreen>(RecordingScreen::class.java) {}
        }
    }

    @Test
    fun `callback exceptions propagate without reflection wrapper`() {
        val expected = IllegalStateException("view failure")
        val screen = object : Screen {
            override fun show(value: String) {
                throw expected
            }
        }
        val presenter = TestPresenter(screen)
        val actual = assertFailsWith<IllegalStateException> { presenter.exposedView.show("fail") }
        assertSame(expected, actual)
    }

    @Test
    fun `generic callback interfaces need no runtime generic resolution`() {
        val values = mutableListOf<String>()
        val screen = object : StringScreen {
            override fun show(value: String) {
                values += value
            }
        }
        val presenter = object : Presenter<StringScreen>(StringScreen::class.java, screen) {
            fun display(value: String) = view.show(value)
        }
        presenter.display("generic")
        assertEquals(listOf("generic"), values)
    }

    @Test
    fun `mapper is callable and maps collections in order`() {
        val mapper = Mapper<Int, String> { "value:$it" }
        assertEquals("value:1", mapper.map(1))
        assertEquals("value:2", mapper(2))
        assertEquals(listOf("value:3", "value:1"), mapper.mapAll(listOf(3, 1)))
        assertEquals(emptyList(), mapper.mapAll(emptyList()))
    }
    @Test
    fun `retaking original view through a replacement survives outer drop cleanup`() {
        val original = RecordingScreen()
        val intermediate = RecordingScreen()
        val presenter = TestPresenter(original)
        var replacing = false
        presenter.dropAction = {
            if (!replacing) {
                replacing = true
                presenter.takeView(intermediate)
                presenter.takeView(original)
            }
        }
        presenter.dropView(original)
        assertTrue(presenter.hasView())
        presenter.exposedView.show("restored")
        assertEquals(listOf("drop", "take", "restored"), original.values)
        assertEquals(listOf("take", "drop"), intermediate.values)
    }

    @Test
    fun `retaking original router through a replacement survives outer drop cleanup`() {
        val original = RecordingNavigation()
        val intermediate = RecordingNavigation()
        val presenter = TestPresenter(initialRouter = original)
        var replacing = false
        presenter.routerDropAction = {
            if (!replacing) {
                replacing = true
                presenter.takeRouter(intermediate)
                presenter.takeRouter(original)
            }
        }
        presenter.dropRouter(original)
        assertTrue(presenter.hasRouter())
        presenter.exposedRouter.go("restored")
        assertEquals(listOf("drop", "take", "restored"), original.values)
        assertEquals(listOf("take", "drop"), intermediate.values)
    }

}
