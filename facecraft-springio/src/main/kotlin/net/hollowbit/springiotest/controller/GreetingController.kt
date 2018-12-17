package net.hollowbit.springiotest.controller

import net.hollowbit.springiotest.model.Greeting
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.concurrent.atomic.AtomicLong


@RestController
class GreetingController {

    companion object {
        const val TEMPLATE = "Hello, %s!";
        val COUNTER = AtomicLong()
    }

    @GetMapping
    fun greeting(@RequestParam(value = "name", defaultValue = "World") name : String) : Greeting =
        Greeting(COUNTER.incrementAndGet(), String.format(TEMPLATE, name))


}